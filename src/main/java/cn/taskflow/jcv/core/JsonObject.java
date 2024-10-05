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

import cn.taskflow.jcv.utils.JsvUtils;

/**
 * 对象ObjectNode参数
 *
 * @author KEVIN LUAN
 */
public class JsonObject extends JsonBasicSchema {

    JsonObject() {
        super();
    }

    public JsonObject(String name, boolean required, String description, JsonSchema[] children) {
        super(name, required, DataType.Object, description);
        if (children != null) {
            this.children = new JsonBasicSchema[children.length];
            for (int i = 0; i < children.length; i++) {
                JsonSchema jsonSchema = children[i];
                this.children[i] = (JsonBasicSchema) jsonSchema;
                if (jsonSchema.isObjectValue()) {
                    throw JsvUtils.throwParamException(jsonSchema.getPath());
                }
            }
        }
    }

    public static JsonObject required(String name, String description, JsonSchema... children) {
        return new JsonObject(name, true, description, children);
    }

    public static JsonObject required(String name, JsonSchema... children) {
        return new JsonObject(name, true, null, children);
    }

    public static JsonObject required(JsonSchema... children) {
        return new JsonObject("", true, null, children);
    }

    public static JsonObject optional(JsonSchema... children) {
        return new JsonObject("", false, null, children);
    }

    public static JsonObject optional(String name, String description, JsonSchema... children) {
        return new JsonObject(name, false, description, children);
    }

    public static JsonObject optional(String name, JsonSchema... children) {
        return new JsonObject(name, false, null, children);
    }

    public boolean isObject() {
        return true;
    }

    @Override
    public JsonObject asObject() {
        return this;
    }

    public final boolean existsChildren() {
        return this.children != null && children.length > 0;
    }

    public JsonSchema[] getChildren() {
        return children;
    }
}
