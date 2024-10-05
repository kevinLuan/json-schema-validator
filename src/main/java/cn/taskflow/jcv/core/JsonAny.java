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

/**
 * 增加JsonAny类型定义
 *
 * @author SHOUSHEN.LUAN
 * @since 2024-08-27
 */
public class JsonAny extends JsonBasicSchema {
    public JsonAny() {
    }

    public JsonAny(String name, boolean required, String description) {
        super(name, required, DataType.Any, description);
    }

    public static JsonAny required(String name, String description) {
        return new JsonAny(name, true, description);
    }

    public static JsonAny optional(String name, String description) {
        return new JsonAny(name, false, description);
    }

    public JsonSchema[] getChildren() {
        return children;
    }

    public void setChildren(JsonBasicSchema[] children) {
        super.children = children;
    }
}
