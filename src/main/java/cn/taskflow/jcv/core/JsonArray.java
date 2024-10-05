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

/**
 * 数组参数类型
 *
 * @author KEVIN LUAN
 */
public class JsonArray extends JsonBasicSchema {

    public JsonArray() {
    }

    public JsonArray(String name, boolean required, String description, JsonBasicSchema childrens) {
        super(name, required, DataType.Array, description);
        check(childrens);
        if (childrens != null) {
            this.children = new JsonBasicSchema[] { childrens };
        }
    }

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
     * 创建一个必须参数
     *
     * @param name
     * @return
     */
    public static JsonArray required(String name, String description, JsonBasicSchema childrens) {
        return new JsonArray(name, true, description, childrens);
    }

    public static JsonArray required(String name, JsonBasicSchema child) {
        return new JsonArray(name, true, null, child);
    }

    /**
     * 创建一个必须的Array节点，任意类型的子节点
     *
     * @param name
     * @return
     */
    public static JsonArray required(String name) {
        return new JsonArray(name, true, null, null);
    }

    public static JsonArray required(String name, String description) {
        return new JsonArray(name, true, description, null);
    }

    public static JsonArray optional(String name, String description, JsonBasicSchema childrens) {
        return new JsonArray(name, false, description, childrens);
    }

    /**
     * 创建一个非必须的Array节点，任意类型的子节点
     *
     * @param name
     * @return
     */
    public static JsonArray optional(String name) {
        return new JsonArray(name, false, null, null);
    }

    public static JsonArray optional(String name, String description) {
        return new JsonArray(name, false, description, null);
    }

    @Override
    public JsonArray asArray() {
        return this;
    }

    public JsonSchema[] getChildren() {
        return children;
    }

    public final JsonSchema getSchemaForFirstChildren() {
        if (existsChildren()) {
            return children[0];
        }
        throw new ValidationException("Missing children", getPath());
    }

    public final boolean existsChildren() {
        return this.children != null && children.length > 0;
    }
}
