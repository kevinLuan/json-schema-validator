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

import cn.taskflow.jcv.core.JsonSchema;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-10-31
 */
@ControllerAdvice
public class CustomRequestBodyAdvice implements RequestBodyAdvice {
    private final JsonSchemaRequestBodyValidator validator;
    private final JsonSchemaFactory              jsonSchemaFactory;

    public CustomRequestBodyAdvice(JsonSchemaRequestBodyValidator validator, JsonSchemaFactory jsonSchemaFactory) {
        this.validator = validator;
        this.jsonSchemaFactory = jsonSchemaFactory;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return findJsv(methodParameter).isPresent();
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                           Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        Optional<JsonSchemaValidate> optional = findJsv(parameter);
        if (optional.isPresent()) {
            if (optional.get().strategy() == ValidationStrategy.BEFORE_BODY_READ) {
                return validator.process(optional.get(), inputMessage);
            }
        }
        return inputMessage;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        findJsv(parameter).ifPresent((jsv) -> {
            if (jsv.strategy() == ValidationStrategy.AFTER_BODY_READ) {
                validator.process(jsv, body);
            }
        });
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        if (body == null) {
            Optional<JsonSchemaValidate> jsonSchemaValidateOpt = findJsv(parameter);
            if (jsonSchemaValidateOpt.isPresent()) {
                Optional<JsonSchema> jsonSchemaOpt = jsonSchemaFactory.getSchema(jsonSchemaValidateOpt.get().value());
                if (jsonSchemaOpt.isPresent()) {
                    if (jsonSchemaOpt.get().isRequired()) {
                        throw new IllegalArgumentException("Request body cannot be empty");
                    }
                }
            }
        } else {
            findJsv(parameter).ifPresent((jsv) -> {
                validator.process(jsv, body);
            });
        }
        return body;
    }

    private Optional<JsonSchemaValidate> findJsv(MethodParameter parameter) {
        JsonSchemaValidate jsv = Optional.ofNullable(parameter.getParameterAnnotation(JsonSchemaValidate.class))
                .orElseGet(() -> parameter.getMethodAnnotation(JsonSchemaValidate.class));
        return Optional.ofNullable(jsv);
    }
}