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
 * Validates REST request body parameters against a predefined JsonSchema.
 * This class is responsible for ensuring that incoming JSON data adheres to the specified schema.
 * It utilizes annotations to determine which methods or parameters require validation.
 * 
 * @author SHOUSHEN.LUAN
 * @since 2024-09-26
 */
public class JsonSchemaRequestBodyValidator {
    private final JsonSchemaFactory jsonSchemaFactory;

    /**
     * Constructor for JsonSchemaRequestBodyValidator.
     * 
     * @param jsonSchemaFactory The factory used to create JsonSchema instances for validation.
     */
    public JsonSchemaRequestBodyValidator(JsonSchemaFactory jsonSchemaFactory) {
        this.jsonSchemaFactory = jsonSchemaFactory;
    }

    /**
     * Finds the JsonSchemaValidate annotation for a given type.
     * 
     * @param type The type to check for the JsonSchemaValidate annotation.
     * @return An Optional containing the JsonSchemaValidate annotation if present.
     */
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

    /**
     * Finds the JsonSchemaValidate annotation for a given handler method.
     * 
     * @param handlerMethod The handler method to check for the JsonSchemaValidate annotation.
     * @return An Optional containing the JsonSchemaValidate annotation if present.
     */
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

    /**
     * Processes the HTTP input message by validating it against the JsonSchema if applicable.
     * 
     * @param type The type of the request body.
     * @param contextClass The context class for the request.
     * @param inputMessage The HTTP input message containing the request body.
     * @return A new HttpInputMessage if validation is performed, otherwise the original input message.
     * @throws IOException If an I/O error occurs during processing.
     */
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

    /**
     * Creates a new HttpInputMessage with the specified JSON content.
     * 
     * @param httpInputMessage The original HTTP input message.
     * @param value The JSON content to include in the new input message.
     * @return A new HttpInputMessage containing the specified JSON content.
     */
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

    /**
     * Reads the content of the HTTP input message as a string.
     * 
     * @param inputMessage The HTTP input message to read.
     * @return The content of the input message as a string.
     * @throws IOException If an I/O error occurs during reading.
     */
    private String readInputMessage(HttpInputMessage inputMessage) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputMessage.getBody(),
            StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}
