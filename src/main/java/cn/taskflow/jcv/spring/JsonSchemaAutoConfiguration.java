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
 * This class is responsible for the auto-configuration of JSON Schema validation components
 * within a Spring application context. It defines beans for JSON Schema validation and 
 * ensures that all required JSON Schemas are defined at application startup.
 * 
 * @author SHOUSHEN.LUAN
 * @since 2024-09-28
 */
public class JsonSchemaAutoConfiguration {
    // Logger for logging error messages related to JSON Schema validation
    final static Logger log = LoggerFactory.getLogger(JsonSchemaValidate.class);

    /**
     * Defines a bean named "empty" that provides a required JSON object schema.
     * 
     * @return a JsonSchema object representing a required JSON object.
     */
    @Bean("empty")
    public JsonSchema partner() {
        return JsonObject.required();
    }

    /**
     * Defines a bean for JsonSchemaRequestBodyValidator which is responsible for validating
     * request bodies against JSON Schemas.
     * 
     * @param jsonSchemaFactory the factory used to create JSON Schema instances.
     * @return a JsonSchemaRequestBodyValidator instance.
     */
    @Bean
    public JsonSchemaRequestBodyValidator jsonSchemaRequestBodyValidator(JsonSchemaFactory jsonSchemaFactory) {
        return new JsonSchemaRequestBodyValidator(jsonSchemaFactory);
    }

    /**
     * Defines a bean for JsonSchemaFactory which is responsible for creating and managing
     * JSON Schema instances within the application context.
     * 
     * @param context the application context used to access other beans and resources.
     * @return a JsonSchemaFactory instance.
     */
    @Bean
    public JsonSchemaFactory jsonSchemaFactory(ApplicationContext context) {
        return new JsonSchemaFactory(context);
    }

    /**
     * Defines an application listener that verifies the presence of JSON Schema definitions
     * for all request mappings when the application context is refreshed. It logs an error
     * and throws an exception if any required JSON Schema is missing.
     * 
     * @param requestBodyValidator the validator used to find JSON Schema validations.
     * @param jsonSchemaFactory the factory used to retrieve JSON Schema instances.
     * @return an ApplicationListener for ContextRefreshedEvent.
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
