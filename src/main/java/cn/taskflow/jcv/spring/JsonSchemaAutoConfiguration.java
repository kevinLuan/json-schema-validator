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

import cn.taskflow.jcv.core.*;
import cn.taskflow.jcv.encode.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;
import java.util.Optional;

import static cn.taskflow.jcv.utils.JsvUtils.f;

/**
 * 该类负责在Spring应用程序上下文中自动配置JSON Schema验证组件。
 * 它定义了用于JSON Schema验证的bean，并确保在应用程序启动时定义所有必需的JSON Schema。
 *
 * @author SHOUSHEN.LUAN
 * @since 2024-09-28
 */
@Configuration
public class JsonSchemaAutoConfiguration {
    final static Logger log = LoggerFactory.getLogger(JsonSchemaValidate.class);
    @Autowired
    private Environment environment;

    /**
     * 定义一个名为"empty"的bean，提供一个必需的JSON对象模式。
     *
     * @return 一个表示必需JSON对象的JsonSchema对象。
     */
    @Bean("empty")
    public JsonSchema partner() {
        return JsonObject.required();
    }

    @Bean
    public JsonNodeConverter jsonNodeConverter(@Autowired(required = false) ObjectMapper objectMapper) {
        Optional<NamingStrategy> optional = NamingStrategy.parser(environment.getProperty("jsv.naming.strategy"));
        NamingStrategy strategy = optional.orElseGet(() -> {
            return objectMapper == null ? NamingStrategy.CAMEL_CASE : NamingStrategy.DEFAULT;
        });
        JsonNodeConverter jsonNodeConverter;
        switch (strategy) {
            case CAMEL_CASE:
                jsonNodeConverter = new DefaultJsonNodeConverter(new CamelCaseObjectMapper());
                break;
            case SNAKE_CASE:
                jsonNodeConverter = new DefaultJsonNodeConverter(new SnakeCaseObjectMapper());
                break;
            default:
                jsonNodeConverter = new DefaultJsonNodeConverter(objectMapper);
                break;
        }

        NodeFactory.setJsonNodeConverter(jsonNodeConverter);
        return jsonNodeConverter;
    }

    @Bean("jcvCustomRequestBodyAdvice")
    public CustomRequestBodyAdvice customRequestBodyAdvice(JsonSchemaRequestBodyValidator validator,
                                                           JsonSchemaFactory jsonSchemaFactory) {
        return new CustomRequestBodyAdvice(validator, jsonSchemaFactory);
    }

    /**
     * 定义一个用于JsonSchemaRequestBodyValidator的bean，该bean负责根据JSON Schema验证请求体。
     *
     * @param jsonSchemaFactory 用于创建JSON Schema实例的工厂。
     * @return 一个JsonSchemaRequestBodyValidator实例。
     */
    @Bean
    public JsonSchemaRequestBodyValidator jsonSchemaRequestBodyValidator(JsonSchemaFactory jsonSchemaFactory) {
        return new JsonSchemaRequestBodyValidator(jsonSchemaFactory);
    }

    /**
     * 定义一个用于JsonSchemaFactory的bean，该bean负责在应用程序上下文中创建和管理JSON Schema实例。
     *
     * @param context 用于访问其他bean和资源的应用程序上下文。
     * @return 一个JsonSchemaFactory实例。
     */
    @Bean
    public JsonSchemaFactory jsonSchemaFactory(ApplicationContext context) {
        return new JsonSchemaFactory(context);
    }

    /**
     * 定义一个应用程序监听器，当应用程序上下文刷新时，验证所有请求映射的JSON Schema定义的存在。
     * 如果缺少任何必需的JSON Schema，它会记录错误并抛出异常。
     *
     * @param requestBodyValidator 用于查找JSON Schema验证的验证器。
     * @param jsonSchemaFactory    用于检索JSON Schema实例的工厂。
     * @return 一个用于ContextRefreshedEvent的ApplicationListener。
     */
    @Bean
    public ApplicationListener<ContextRefreshedEvent> verifySchemaDefinition(JsonSchemaRequestBodyValidator requestBodyValidator, JsonSchemaFactory jsonSchemaFactory) {
        return (event) -> {
            RequestMappingHandlerMapping handlerMapping = event.getApplicationContext().getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
            Map<RequestMappingInfo, HandlerMethod> map = handlerMapping.getHandlerMethods();
            map.forEach((key, value) -> {
                Optional<JsonSchemaValidate> optional = requestBodyValidator.findJsonSchemaValidate(value);
                optional.ifPresent((jsv) -> {
                    if (!jsonSchemaFactory.getSchema(jsv.value()).isPresent()) {
                        log.error("URL:{},Controller:{},Method:{},schema:{}", key, value.getBeanType().getSimpleName()
                                , value.getMethod().getName(), jsv);
                        throw new IllegalStateException(f("No definition for JsonSchema: '%s' was found", jsv.value()));
                    }
                });
            });
        };
    }
}
