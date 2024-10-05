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
import cn.taskflow.jcv.core.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-09-28
 */
public class JsonSchemaFactory {
    private final Map<String, JsonSchema> schemaMap;

    public JsonSchemaFactory(ApplicationContext context) {
        schemaMap = Collections.unmodifiableMap(context.getBeansOfType(JsonSchema.class));
    }

    public Optional<JsonSchema> getSchema(String schemaName) {
        return Optional.ofNullable(schemaMap.get(schemaName));
    }

    /**
     * 数据验证操作
     *
     * @param schemaValidate
     * @param json
     */
    public void validate(JsonSchemaValidate schemaValidate, String json) {
        Optional<JsonSchema> optional = getSchema(schemaValidate.value());
        if (optional.isPresent()) {
            try {
                Validator.of(optional.get()).validate(json).extract(json);
            } catch (Exception e) {
                if (IllegalArgumentException.class.isAssignableFrom(e.getClass())) {
                    throw (IllegalArgumentException) e;
                } else {
                    throw new IllegalArgumentException(e.getMessage(), e);
                }
            }
        } else {
            throw new IllegalArgumentException(
                String.format("schema:'%s' definition not found", schemaValidate.value()));
        }
    }
}
