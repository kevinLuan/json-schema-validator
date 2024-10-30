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
 * 定义JsonAny类型，该类型扩展了JsonBasicSchema类。
 * 此类表示可以接受任何数据类型的JSON模式类型。
 * 它提供构造函数和方法来指定模式是必需的还是可选的。
 * 
 * @author SHOUSHEN.LUAN
 * @since 2024-08-27
 */
public class JsonAny extends JsonBasicSchema {

    /**
     * JsonAny的默认构造函数。
     */
    public JsonAny() {
    }

    /**
     * 构造具有指定名称、需求状态和描述的JsonAny对象。
     *
     * @param name        JSON模式的名称
     * @param required    一个布尔值，指示模式是否为必需
     * @param description JSON模式的描述
     */
    public JsonAny(String name, boolean required, String description) {
        super(name, required, DataType.Any, description);
    }

    /**
     * 创建具有指定名称和描述的必需JsonAny模式。
     *
     * @param name        JSON模式的名称
     * @param description JSON模式的描述
     * @return 一个标记为必需的JsonAny新实例
     */
    public static JsonAny required(String name, String description) {
        return new JsonAny(name, true, description);
    }

    /**
     * 创建具有指定名称和描述的可选JsonAny模式。
     *
     * @param name        JSON模式的名称
     * @param description JSON模式的描述
     * @return 一个标记为可选的JsonAny新实例
     */
    public static JsonAny optional(String name, String description) {
        return new JsonAny(name, false, description);
    }

    /**
     * 获取此JsonAny实例的子模式。
     *
     * @return 表示子模式的JsonSchema对象数组
     */
    public JsonSchema[] getChildren() {
        return children;
    }

    /**
     * 为此JsonAny实例设置子模式。
     *
     * @param children 要设置为子模式的JsonBasicSchema对象数组
     */
    public void setChildren(JsonBasicSchema[] children) {
        super.children = children;
    }
}
