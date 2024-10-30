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

import cn.taskflow.jcv.validation.EnumValidation;

/**
 * 表示验证框架中的JSON字符串类型。
 * 该类扩展了Primitive类，以提供特定于JSON字符串处理的附加功能。
 * 
 * @autor SHOUSHEN.LUAN
 * @since 2024-02-03
 */
public class JsonString extends Primitive {
    /**
     * 使用指定的参数构造JsonString对象。
     *
     * @param name        JSON字符串的名称
     * @param require     JSON字符串是否是必需的
     * @param dataType    JSON字符串的数据类型
     * @param description JSON字符串的描述
     * @param min         最小值约束（不适用于字符串）
     * @param max         最大值约束（不适用于字符串）
     */
    public JsonString(String name, boolean require, DataType dataType, String description, Number min, Number max) {
        super(name, require, dataType, description, min, max);
    }

    /**
     * 创建一个必需的JsonString参数。
     *
     * @param name JSON字符串的名称
     * @return 一个标记为必需的JsonString实例
     */
    public static JsonString required(String name) {
        return new JsonString(name, true, DataType.String, null, null, null);
    }

    /**
     * 创建一个带有描述的必需JsonString参数。
     *
     * @param name        JSON字符串的名称
     * @param description JSON字符串的描述
     * @return 一个标记为必需且带有描述的JsonString实例
     */
    public static JsonString required(String name, String description) {
        return new JsonString(name, true, DataType.String, description, null, null);
    }

    /**
     * 创建一个非空的JsonString参数。
     * 这种基本类型只能在父节点是数组时使用，例如：array[0,1,2]。
     *
     * @return 一个标记为非空的JsonString实例
     */
    public static JsonString ofNonNull() {
        return new JsonString("", true, DataType.String, null, null, null);
    }

    /**
     * 创建一个可为空的JsonString参数。
     *
     * @return 一个标记为可空的JsonString实例
     */
    public static JsonString ofNullable() {
        return new JsonString("", false, DataType.String, null, null, null);
    }

    /**
     * 创建一个带有描述的可选JsonString参数。
     *
     * @param name        JSON字符串的名称
     * @param description JSON字符串的描述
     * @return 一个标记为可选且带有描述的JsonString实例
     */
    public static JsonString optional(String name, String description) {
        return new JsonString(name, false, DataType.String, description, null, null);
    }

    /**
     * 创建一个可选的JsonString参数。
     *
     * @param name JSON字符串的名称
     * @return 一个标记为可选的JsonString实例
     */
    public static JsonString optional(String name) {
        return new JsonString(name, false, DataType.String, null, null, null);
    }

    /**
     * 指定此JsonString的值必须在给定的枚举范围内。
     *
     * @param enumClass 要验证的枚举类
     * @param <E>       枚举的类型
     * @return 此JsonString实例，用于方法链
     */
    public <E extends Enum<E>> JsonString inEnum(Class<E> enumClass) {
        return this.withValidator((schema, node) -> {
            if (node == null || node.isNull()) {
                return true; // 支持可选字段，null是有效的
            }
            try {
                Enum.valueOf(enumClass, node.asText());
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        });
    }

    /**
     * 指定此JsonString的值必须在给定的枚举值数组内。
     *
     * @param values 要验证的枚举值数组
     * @param <E>    枚举的类型
     * @return 此JsonString实例，用于方法链
     * @throws IllegalArgumentException 如果值数组为null或为空
     */
    public <E extends Enum<E>> JsonString inEnum(E[] values) {
        if (values == null || values.length < 1) {
            throw new IllegalArgumentException("The parameter cannot be null or empty");
        }
        return withValidator(EnumValidation.of(values));
    }
}
