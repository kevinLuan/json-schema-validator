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
/*
 * 根据一个或多个贡献者许可协议，授权给Apache软件基金会（ASF）。
 * 请参阅随本作品分发的NOTICE文件，以获取有关版权归属的其他信息。
 * 根据Apache许可证2.0版（“许可证”），ASF授权您使用此文件；
 * 除非符合许可证，否则您不得使用此文件。您可以在以下位置获取许可证副本：
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非适用法律要求或书面同意，否则根据许可证分发的软件按“原样”分发，
 * 不提供任何明示或暗示的担保或条件。
 * 请参阅许可证以了解管理权限和限制的特定语言。
 */
package cn.taskflow.jcv.core;

import cn.taskflow.jcv.exception.NotSupportedException;
import cn.taskflow.jcv.exception.ValidationException;
import cn.taskflow.jcv.utils.CodeGenerationUtils;
import cn.taskflow.jcv.utils.JsvUtils;

/**
 * 枚举表示可用于JSON模式验证的不同数据类型。
 * 每种数据类型都提供特定的方法来根据其类型检查和验证值。
 */
public enum DataType {
    String {
        @Override
        public boolean isString() {
            return true;
        }

        /**
         * 根据Primitive对象中定义的约束验证字符串值。
         * 检查空值、最小和最大长度约束。
         *
         * @param primitive 包含验证约束的原始模式
         * @param value     要验证的字符串值
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
         * 验证表示为字符串的布尔值。
         * 接受“true”、“false”、“1”和“0”作为有效的布尔表示。
         *
         * @param primitive 包含验证约束的原始模式
         * @param value     要验证的字符串值
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
     * 确定给定的数据类型是否为原始类型（String、Number或Boolean）。
     *
     * @param dataType 要检查的数据类型
     * @return 如果数据类型是原始类型，则返回true，否则返回false
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
     * 子类实现特定验证逻辑的占位符方法。
     *
     * @param p     包含验证约束的原始模式
     * @param value 要验证的值
     */
    public void check(Primitive p, String value) {
        // TODO 子类实现
    }

    /**
     * 解析数据类型的字符串表示并返回相应的DataType枚举。
     *
     * @param dataType 数据类型的字符串表示
     * @return 相应的DataType枚举
     * @throws IllegalArgumentException 如果数据类型无效
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
     * 生成用于创建基于数据类型和需求的原始JSON模式元素的代码。
     *
     * @param required 元素是否是必需的
     * @return 生成的代码作为字符串
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
     * 生成用于创建具有附加元数据的原始JSON模式元素的代码。
     *
     * @param required 元素是否是必需的
     * @param name     元素的名称
     * @param desc     元素的描述
     * @return 生成的代码作为字符串
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
