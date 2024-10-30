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

import cn.taskflow.jcv.exception.ValidationException;
import cn.taskflow.jcv.utils.StringUtils;

import java.util.Optional;

/**
 * 表示JSON模式中的数组参数类型。
 * 此类用于定义JSON数组的结构和验证规则。
 * 它扩展了JsonBasicSchema类以继承通用模式属性。
 * 
 * 作者：KEVIN LUAN
 */
public class JsonArray extends JsonBasicSchema {

    /**
     * JsonArray的默认构造函数。
     */
    public JsonArray() {
    }

    /**
     * 构造具有指定属性的JsonArray。
     *
     * @param name        数组的名称
     * @param required    数组是否是必需的
     * @param description 数组的描述
     * @param child       子元素的模式
     */
    public JsonArray(String name, boolean required, String description, JsonBasicSchema child) {
        super(name, required, DataType.Array, description);
        check(child);
        if (child != null) {
            this.children = new JsonBasicSchema[] { child };
        }
    }

    /**
     * 验证子模式。
     * 确保子模式不是数组并且没有名称。
     *
     * @param childrens 要验证的子模式
     * @throws ValidationException 如果子模式无效
     */
    private void check(JsonSchema childrens) {
        if (childrens != null) {
            if (childrens.getDataType() == DataType.Array) {
                throw new ValidationException("illegal parameter", childrens.getPath());
            } else {
                if (StringUtils.isNotBlank(childrens.getName())) {
                    throw new ValidationException("parameter error", childrens.getPath());
                }
            }
        }
    }

    /**
     * 创建具有指定名称、描述和子模式的必需JsonArray。
     *
     * @param name        数组的名称
     * @param description 数组的描述
     * @param childrens   子元素的模式
     * @return JsonArray的新实例
     */
    public static JsonArray required(String name, String description, JsonBasicSchema childrens) {
        return new JsonArray(name, true, description, childrens);
    }

    /**
     * 创建具有指定名称和子模式的必需JsonArray。
     *
     * @param name  数组的名称
     * @param child 子元素的模式
     * @return JsonArray的新实例
     */
    public static JsonArray required(String name, JsonBasicSchema child) {
        return new JsonArray(name, true, null, child);
    }

    /**
     * 创建具有指定名称的必需JsonArray。
     * 数组可以具有任何类型的子节点。
     *
     * @param name 数组的名称
     * @return JsonArray的新实例
     */
    public static JsonArray required(String name) {
        return new JsonArray(name, true, null, null);
    }

    /**
     * 创建具有指定名称和描述的必需JsonArray。
     *
     * @param name        数组的名称
     * @param description 数组的描述
     * @return JsonArray的新实例
     */
    public static JsonArray required(String name, String description) {
        return new JsonArray(name, true, description, null);
    }

    /**
     * 创建具有指定名称、描述和子模式的可选JsonArray。
     *
     * @param name        数组的名称
     * @param description 数组的描述
     * @param child       子元素的模式
     * @return JsonArray的新实例
     */
    public static JsonArray optional(String name, String description, JsonBasicSchema child) {
        return new JsonArray(name, false, description, child);
    }

    /**
     * 创建具有指定名称和子模式的可选JsonArray。
     *
     * @param name  数组的名称
     * @param child 子元素的模式
     * @return JsonArray的新实例
     */
    public static JsonArray optional(String name, JsonBasicSchema child) {
        return new JsonArray(name, false, null, child);
    }

    /**
     * 创建具有指定名称的可选JsonArray。
     * 数组可以具有任何类型的子节点。
     *
     * @param name 数组的名称
     * @return JsonArray的新实例
     */
    public static JsonArray optional(String name) {
        return new JsonArray(name, false, null, null);
    }

    /**
     * 创建具有指定名称和描述的可选JsonArray。
     *
     * @param name        数组的名称
     * @param description 数组的描述
     * @return JsonArray的新实例
     */
    public static JsonArray optional(String name, String description) {
        return new JsonArray(name, false, description, null);
    }

    /**
     * 将此实例作为JsonArray返回。
     *
     * @return 此JsonArray实例
     */
    @Override
    public JsonArray asArray() {
        return this;
    }

    /**
     * 获取此JsonArray的子模式。
     *
     * @return 子模式数组
     */
    public JsonSchema[] getChildren() {
        return children;
    }

    /**
     * 获取第一个子元素的模式（如果存在）。
     *
     * @return 包含第一个子模式的Optional，如果不存在子元素则为空
     */
    public final Optional<JsonSchema> getSchemaForFirstChildren() {
        if (existsChildren()) {
            return Optional.of(children[0]);
        }
        return Optional.empty();
    }

    /**
     * Checks if this JsonArray has any child schemas.
     *
     * @return true if there are child schemas, false otherwise
     */
    public final boolean existsChildren() {
        return this.children != null && children.length > 0;
    }
}
