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

public enum DataType {
    String {
        @Override
        public boolean isString() {
            return true;
        }

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
     * 断言基本数据类型 (String,Number)
     *
     * @param dataType
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

    public void check(Primitive p, String value) {
        // TODO 子类实现
    }

    public static DataType parser(String dataType) {
        for (DataType type : values()) {
            if (type.name().equals(dataType)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid type: " + dataType);
    }

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
