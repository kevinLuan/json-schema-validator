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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Customizes Spring's message converters to include a JSON schema validator.
 * This configuration class modifies the default behavior of Spring's HTTP message converters
 * by adding a custom converter that validates request bodies against a JSON schema.
 * 
 * The configuration is activated based on a property key, allowing for dynamic enabling or disabling
 * of the request body validation feature.
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
    /* Property key to enable or disable request body validation */
    private final String                   key = "cn.taskflow.jsv.validation.request-body.enabled";

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // Check if request body validation is enabled via the environment property
        if (environment.getProperty(key, Boolean.class, true)) {
            // Add the custom message converter at the beginning of the converters list
            converters.add(0, new CustomHttpMessageConverter(jsonSchemaRequestBodyValidator, objectMapper));
        }
    }
}
