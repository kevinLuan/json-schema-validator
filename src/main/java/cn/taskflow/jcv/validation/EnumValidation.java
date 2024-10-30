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
 * 枚举验证器，用于验证值是否在预定义的允许值集合中
 *
 * @author SHOUSHEN.LUAN
 * @since 2024-10-26
 */
public class EnumValidation implements CustomValidationRule {
    // Array to store the allowed enum values
    // 用于存储允许的枚举值的数组
    private final Object[] values;

    /**
     * Constructor to initialize the enum validator with allowed values
     * 构造函数，用于使用允许的值初始化枚举验证器
     * @param values Array of allowed enum values
     *               允许的枚举值数组
     */
    public EnumValidation(Object[] values) {
        this.values = values;
    }

    /**
     * Static factory method to create an enum validator
     * 静态工厂方法，用于创建枚举验证器
     * @param values Array of allowed enum values
     *               允许的枚举值数组
     * @return New EnumValidation instance
     *         新的 EnumValidation 实例
     */
    public static EnumValidation of(Object[] values) {
        return new EnumValidation(values);
    }

    /**
     * Validates if the given node value matches any of the allowed enum values
     * 验证给定节点值是否与任何允许的枚举值匹配
     * @param schema The JSON schema definition
     *               JSON 模式定义
     * @param node The node to validate
     *             要验证的节点
     * @return true if value is valid, false otherwise
     *         如果值有效则返回 true，否则返回 false
     * @throws ValidationException if validation fails
     *                             如果验证失败则抛出 ValidationException
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
