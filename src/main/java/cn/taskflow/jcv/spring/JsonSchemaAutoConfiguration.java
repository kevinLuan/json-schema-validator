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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;
import java.util.Optional;

import static cn.taskflow.jcv.utils.JsvUtils.f;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-09-28
 */
public class JsonSchemaAutoConfiguration {
    final static Logger log = LoggerFactory.getLogger(JsonSchemaValidate.class);

    @Bean("empty")
    public JsonSchema partner() {
        return JsonObject.required();
    }

    @Bean
    public JsonSchemaRequestBodyValidator jsonSchemaRequestBodyValidator(JsonSchemaFactory jsonSchemaFactory) {
        return new JsonSchemaRequestBodyValidator(jsonSchemaFactory);
    }

    @Bean
    public JsonSchemaFactory jsonSchemaFactory(ApplicationContext context) {
        return new JsonSchemaFactory(context);
    }

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
