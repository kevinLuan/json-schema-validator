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
package cn.taskflow.jcv.validation;

import cn.taskflow.jcv.core.JsonSchema;
import cn.taskflow.jcv.exception.ValidationException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Enum validator for validating values against a predefined set of allowed values
 *
 * @author SHOUSHEN.LUAN
 * @since 2024-10-26
 */
public class EnumValidation implements CustomValidationRule {
    // Array to store the allowed enum values
    private final Object[] values;

    /**
     * Constructor to initialize the enum validator with allowed values
     * @param values Array of allowed enum values
     */
    public EnumValidation(Object[] values) {
        this.values = values;
    }

    /**
     * Static factory method to create an enum validator
     * @param values Array of allowed enum values
     * @return New EnumValidation instance
     */
    public static EnumValidation of(Object[] values) {
        return new EnumValidation(values);
    }

    /**
     * Validates if the given node value matches any of the allowed enum values
     * @param schema The JSON schema definition
     * @param node The node to validate
     * @return true if value is valid, false otherwise
     * @throws ValidationException if validation fails
     */
    @Override
    public boolean validate(JsonSchema schema, JsonNode node) throws ValidationException {
        if (node != null && !node.isNull()) {
            for (Object value : values) {
                if (value.toString().equals(node.asText())) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
}
