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
 * @author SHOUSHEN.LUAN
 * @since 2024-02-03
 */
public class JsonNumber extends Primitive {
    public JsonNumber(String name, boolean require, DataType dataType, String description, Number min, Number max) {
        super(name, require, dataType, description, min, max);
    }

    /**
     * 创建一个必须参数
     *
     * @param name
     * @return
     */
    public static JsonNumber required(String name, String description) {
        return new JsonNumber(name, true, DataType.Number, description, null, null);
    }

    public static JsonNumber required(String name) {
        return new JsonNumber(name, true, DataType.Number, null, null, null);
    }

    /**
     * 创建一个必须参数
     * <p>
     * 当前基本类型只能用在父节点是Array的情况例如：array[0,1,2]
     *
     * @return
     */
    public static JsonNumber ofNonNull() {
        return new JsonNumber("", true, DataType.Number, null, null, null);
    }

    public static JsonNumber ofNullable() {
        return new JsonNumber("", false, DataType.Number, null, null, null);
    }

    public static JsonNumber optional(String name) {
        return new JsonNumber(name, false, DataType.Number, null, null, null);
    }

    public static JsonNumber optional(String name, String description) {
        return new JsonNumber(name, false, DataType.Number, description, null, null);
    }

}
