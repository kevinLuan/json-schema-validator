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
package cn.taskflow.jcv.core;

import cn.taskflow.jcv.exception.NotSupportedException;
import cn.taskflow.jcv.exception.ValidationException;
import cn.taskflow.jcv.utils.CodeGenerationUtils;
import cn.taskflow.jcv.utils.JsvUtils;

/**
 * Enum representing different data types that can be used in JSON schema validation.
 * Each data type provides specific methods to check and validate values according to its type.
 */
public enum DataType {
    String {
        @Override
        public boolean isString() {
            return true;
        }

        /**
         * Validates a string value against the constraints defined in the Primitive object.
         * Checks for null values, minimum and maximum length constraints.
         *
         * @param primitive the primitive schema containing validation constraints
         * @param value     the string value to validate
         */
        @Override
        public void check(Primitive primitive, String value) {
            if (primitive.isRequired()) {
                if (value == null) {
                    throw JsvUtils.throwMissingParamException(primitive.getName());
                }
            } else {
                if (value == null) {
                    return;
                }
            }
            if (primitive.getMin() != null) {
                if (primitive.getMin().intValue() > value.length()) {
                    throw new ValidationException(primitive.getTipMsg(), primitive.getPath());
                }
            }
            if (primitive.getMax() != null) {
                if (primitive.getMax().intValue() < value.length()) {
                    throw new ValidationException(primitive.getTipMsg(), primitive.getPath());
                }
            }
        }
    },
    Number {
        @Override
        public boolean isNumber() {
            return true;
        }
    },
    Boolean {
        @Override
        public boolean isBoolean() {
            return true;
        }

        /**
         * Validates a boolean value represented as a string.
         * Accepts "true", "false", "1", and "0" as valid boolean representations.
         *
         * @param primitive the primitive schema containing validation constraints
         * @param value     the string value to validate
         */
        @Override
        public void check(Primitive primitive, String value) {
            if (primitive.isRequired()) {
                if (value == null) {
                    throw JsvUtils.throwMissingParamException(primitive.getName());
                }
            } else {
                if (value == null) {
                    return;
                }
            }
            if (!"true".equals(value) && !"false".equals(value) && !"1".equals(value) && !"0".equals(value)) {
                throw JsvUtils.throwParamException(primitive.getName());
            }
        }
    },
    Array, Object, Any;

    /**
     * Determines if the given data type is a primitive type (String, Number, or Boolean).
     *
     * @param dataType the data type to check
     * @return true if the data type is primitive, false otherwise
     */
    public static boolean isPrimitive(DataType dataType) {
        if (dataType != null && (DataType.String == dataType || DataType.Number == dataType)
            || DataType.Boolean == dataType) {
            return true;
        }
        return false;
    }

    public boolean isNumber() {
        return false;
    }

    public boolean isString() {
        return false;
    }

    public boolean isBoolean() {
        return false;
    }

    /**
     * Placeholder method for subclasses to implement specific validation logic.
     *
     * @param p     the primitive schema containing validation constraints
     * @param value the value to validate
     */
    public void check(Primitive p, String value) {
        // TODO 子类实现
    }

    /**
     * Parses a string representation of a data type and returns the corresponding DataType enum.
     *
     * @param dataType the string representation of the data type
     * @return the corresponding DataType enum
     * @throws IllegalArgumentException if the data type is invalid
     */
    public static DataType parser(String dataType) {
        for (DataType type : values()) {
            if (type.name().equals(dataType)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid type: " + dataType);
    }

    /**
     * Generates code for creating a primitive JSON schema element based on the data type and requirement.
     *
     * @param required whether the element is required
     * @return the generated code as a string
     */
    public String generatePrimitiveCode(boolean required) {
        switch (this) {
            case Number:
                if (required) {
                    return "JsonNumber.make()";
                } else {
                    return "JsonNumber.ofNullable()";
                }
            case String:
                if (required) {
                    return "JsonString.make()";
                } else {
                    return "JsonString.ofNullable()";
                }
            case Boolean:
                if (required) {
                    return "JsonBoolean.make()";
                } else {
                    return "JsonBoolean.ofNullable()";
                }
            default:
                throw new NotSupportedException("Invalid type: " + this);
        }
    }

    /**
     * Generates code for creating a primitive JSON schema element with additional metadata.
     *
     * @param required whether the element is required
     * @param name     the name of the element
     * @param desc     the description of the element
     * @return the generated code as a string
     */
    public String generatePrimitiveCode(boolean required, String name, String desc) {
        final String code;
        switch (this) {
            case Number:
                if (required || CodeGenerationUtils.getOptional().isRequire()) {
                    if (CodeGenerationUtils.getOptional().isGenerateDesc()) {
                        code = JsvUtils.f("JsonNumber.required('%s',%s)", name, JsvUtils.formatStringArgs(desc));
                    } else {
                        code = JsvUtils.f("JsonNumber.required('%s')", name);
                    }
                } else {
                    if (CodeGenerationUtils.getOptional().isGenerateDesc()) {
                        code = JsvUtils.f("JsonNumber.optional('%s',%s)", name, JsvUtils.formatStringArgs(desc));
                    } else {
                        code = JsvUtils.f("JsonNumber.optional('%s')", name);
                    }
                }
                break;
            case String:
                if (required || CodeGenerationUtils.getOptional().isRequire()) {
                    if (CodeGenerationUtils.getOptional().isGenerateDesc()) {
                        code = JsvUtils.f("JsonString.required('%s',%s)", name, JsvUtils.formatStringArgs(desc));
                    } else {
                        code = JsvUtils.f("JsonString.required('%s')", name);
                    }
                } else {
                    if (CodeGenerationUtils.getOptional().isGenerateDesc()) {
                        code = JsvUtils.f("JsonString.optional('%s',%s)", name, JsvUtils.formatStringArgs(desc));
                    } else {
                        code = JsvUtils.f("JsonString.optional('%s')", name);
                    }
                }
                break;
            case Boolean:
                if (required || CodeGenerationUtils.getOptional().isRequire()) {
                    if (CodeGenerationUtils.getOptional().isGenerateDesc()) {
                        code = JsvUtils.f("JsonBoolean.required('%s',%s)", name, JsvUtils.formatStringArgs(desc));
                    } else {
                        code = JsvUtils.f("JsonBoolean.required('%s')", name);
                    }
                } else {
                    if (CodeGenerationUtils.getOptional().isGenerateDesc()) {
                        code = JsvUtils.f("JsonBoolean.optional('%s',%s)", name, JsvUtils.formatStringArgs(desc));
                    } else {
                        code = JsvUtils.f("JsonBoolean.optional('%s')", name);
                    }
                }
                break;
            default:
                throw new NotSupportedException("Invalid type: " + this);
        }
        return code.replace('\'', '"');
    }
}
