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

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.web.servlet.HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE;

/**
 * 根据预定义的JsonSchema对REST 请求body参数验证处理
 *
 * @author SHOUSHEN.LUAN
 * @since 2024-09-26
 */
public class JsonSchemaRequestBodyValidator {
    private final JsonSchemaFactory jsonSchemaFactory;

    public JsonSchemaRequestBodyValidator(JsonSchemaFactory jsonSchemaFactory) {
        this.jsonSchemaFactory = jsonSchemaFactory;
    }

    private Optional<JsonSchemaValidate> findJsonSchemaValidate(Type type) {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HandlerMethod handlerMethod = (HandlerMethod) sra.getRequest().getAttribute(BEST_MATCHING_HANDLER_ATTRIBUTE);
        if (handlerMethod.hasMethodAnnotation(JsonSchemaValidate.class)) {
            return Optional.of(handlerMethod.getMethodAnnotation(JsonSchemaValidate.class));
        }
        for (MethodParameter methodParameter : handlerMethod.getMethodParameters()) {
            if (methodParameter.getGenericParameterType().equals(type)) {
                if (methodParameter.hasParameterAnnotation(JsonSchemaValidate.class)) {
                    return Optional.ofNullable(methodParameter.getParameterAnnotation(JsonSchemaValidate.class));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<JsonSchemaValidate> findJsonSchemaValidate(HandlerMethod handlerMethod) {
        if (handlerMethod.hasMethodAnnotation(JsonSchemaValidate.class)) {
            return Optional.of(handlerMethod.getMethodAnnotation(JsonSchemaValidate.class));
        }
        for (MethodParameter methodParameter : handlerMethod.getMethodParameters()) {
            if (methodParameter.hasParameterAnnotation(JsonSchemaValidate.class)) {
                return Optional.ofNullable(methodParameter.getParameterAnnotation(JsonSchemaValidate.class));
            }
        }
        return Optional.empty();
    }

    public HttpInputMessage process(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException {
        Optional<JsonSchemaValidate> optional = findJsonSchemaValidate(type);
        if (optional.isPresent()) {
            String json = readInputMessage(inputMessage);
            jsonSchemaFactory.validate(optional.get(), json);
            return createHttpInputMessage(inputMessage, json);
        } else {
            return inputMessage;
        }
    }

    private HttpInputMessage createHttpInputMessage(HttpInputMessage httpInputMessage, String value) {
        return new HttpInputMessage() {
            @Override
            public HttpHeaders getHeaders() {
                return httpInputMessage.getHeaders();
            }

            @Override
            public InputStream getBody() throws IOException {
                return new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
            }
        };
    }

    private String readInputMessage(HttpInputMessage inputMessage) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputMessage.getBody(),
            StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}
