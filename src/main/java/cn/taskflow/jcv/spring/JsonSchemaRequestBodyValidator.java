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
 * 验证REST请求体参数是否符合预定义的JsonSchema。
 * 该类负责确保传入的JSON数据符合指定的模式。
 * 它利用注解来确定哪些方法或参数需要验证。
 * 
 * @作者 SHOUSHEN.LUAN
 * @自 2024-09-26
 */
public class JsonSchemaRequestBodyValidator {
    private final JsonSchemaFactory jsonSchemaFactory;

    /**
     * JsonSchemaRequestBodyValidator的构造函数。
     * 
     * @param jsonSchemaFactory 用于创建JsonSchema实例以进行验证的工厂。
     */
    public JsonSchemaRequestBodyValidator(JsonSchemaFactory jsonSchemaFactory) {
        this.jsonSchemaFactory = jsonSchemaFactory;
    }

    /**
     * 查找给定类型的JsonSchemaValidate注解。
     * 
     * @param type 要检查JsonSchemaValidate注解的类型。
     * @return 如果存在，返回包含JsonSchemaValidate注解的Optional。
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
     * 查找给定处理方法的JsonSchemaValidate注解。
     * 
     * @param handlerMethod 要检查JsonSchemaValidate注解的处理方法。
     * @return 如果存在，返回包含JsonSchemaValidate注解的Optional。
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
     * 通过验证HTTP输入消息来处理它，如果适用的话，验证它是否符合JsonSchema。
     * 
     * @param type 请求体的类型。
     * @param contextClass 请求的上下文类。
     * @param inputMessage 包含请求体的HTTP输入消息。
     * @return 如果执行了验证，返回新的HttpInputMessage，否则返回原始输入消息。
     * @throws IOException 如果在处理过程中发生I/O错误。
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
     * 创建一个包含指定JSON内容的新HttpInputMessage。
     * 
     * @param httpInputMessage 原始HTTP输入消息。
     * @param value 要包含在新输入消息中的JSON内容。
     * @return 包含指定JSON内容的新HttpInputMessage。
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
     * 将HTTP输入消息的内容读取为字符串。
     * 
     * @param inputMessage 要读取的HTTP输入消息。
     * @return 输入消息的内容作为字符串。
     * @throws IOException 如果在读取过程中发生I/O错误。
     */
    private String readInputMessage(HttpInputMessage inputMessage) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputMessage.getBody(),
            StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}
