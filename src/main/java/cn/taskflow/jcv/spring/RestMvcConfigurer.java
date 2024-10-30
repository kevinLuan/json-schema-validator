/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.taskflow.jcv.spring;

import cn.taskflow.jcv.encode.CamelCaseObjectMapper;
import cn.taskflow.jcv.encode.DefaultJsonNodeConverter;
import cn.taskflow.jcv.encode.NodeFactory;
import cn.taskflow.jcv.encode.SnakeCaseObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 自定义Spring的消息转换器以包含JSON模式验证器。
 * 这个配置类通过添加一个自定义转换器来修改Spring的HTTP消息转换器的默认行为，
 * 该转换器根据JSON模式验证请求体。
 * <p>
 * 该配置基于一个属性键激活，允许动态启用或禁用请求体验证功能。
 *
 * @author SHOUSHEN.LUAN
 * @since 2024-10-27
 */
@Configuration
public class RestMvcConfigurer implements WebMvcConfigurer {
    @Autowired
    private JsonSchemaRequestBodyValidator jsonSchemaRequestBodyValidator;
    @Autowired(required = false)
    private ObjectMapper                   objectMapper;
    @Autowired
    private Environment                    environment;
    /* 用于启用或禁用请求体验证的属性键 */
    private final String                   key = "cn.taskflow.jsv.validation.request-body.enabled";

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 检查是否通过环境属性启用了请求体验证
        if (environment.getProperty(key, Boolean.class, true)) {
            String namingStrategy = environment.getProperty("naming.strategy", "camelCase");
            if ("snakeCase".equals(namingStrategy)) {
                objectMapper = new SnakeCaseObjectMapper();
            } else if ("camelCase".equals(namingStrategy) || objectMapper == null) {
                objectMapper = new CamelCaseObjectMapper();
            }
            NodeFactory.setJsonNodeConverter(new DefaultJsonNodeConverter(objectMapper));
            // 在转换器列表的开头添加自定义消息转换器
            converters.add(0, new CustomHttpMessageConverter(jsonSchemaRequestBodyValidator, objectMapper));
        }
    }
}
