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
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Custom HTTP request message converter.
 * This class extends the MappingJackson2HttpMessageConverter to include
 * additional functionality for validating JSON request bodies against a schema.
 * It uses a JsonSchemaRequestBodyValidator to process and validate the input message.
 * 
 * @author SHOUSHEN.LUAN
 * @since 2024-09-25
 */
@Deprecated
class CustomHttpMessageConverter extends MappingJackson2HttpMessageConverter {
    private final JsonSchemaRequestBodyValidator jsonSchemaRequestBodyValidator;

    /**
     * Constructor for CustomHttpMessageConverter.
     * 
     * @param jsonSchemaRequestBodyValidator the validator used to process and validate JSON request bodies
     * @param objectMapper the ObjectMapper used for JSON conversion
     */
    public CustomHttpMessageConverter(JsonSchemaRequestBodyValidator jsonSchemaRequestBodyValidator,
                                      ObjectMapper objectMapper) {
        super(objectMapper);
        this.jsonSchemaRequestBodyValidator = jsonSchemaRequestBodyValidator;
    }

    /**
     * Reads and processes the HTTP input message.
     * This method overrides the read method to include JSON schema validation
     * before delegating to the superclass's read method.
     * 
     * @param type the type of the object to return
     * @param contextClass the context class for the conversion
     * @param inputMessage the HTTP input message to read from
     * @return the converted object
     * @throws IOException if an I/O error occurs
     * @throws HttpMessageNotReadableException if the message is not readable
     */
    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException,
                                                                                       HttpMessageNotReadableException {
        inputMessage = jsonSchemaRequestBodyValidator.process(type, contextClass, inputMessage);
        return super.read(type, contextClass, inputMessage);
    }
}