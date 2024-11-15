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
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定义JSON模式参数的接口。
 * 该接口提供了与JSON模式元素交互和验证的方法。
 * 它支持检查类型、转换类型以及设置或获取父节点的操作。
 * 还包括对JSON节点进行自定义验证的方法。
 * 
 * 作者: KEVIN LUAN
 */
public interface JsonSchema extends Cloneable {
    Logger LOG = LoggerFactory.getLogger(JsonSchema.class);

    /**
     * 获取参数的名称。
     *
     * @return 参数的名称
     */
    String getName();

    /**
     * 检查参数是否为原始类型。
     *
     * @return 如果参数是原始类型则返回true，否则返回false
     */
    boolean isPrimitive();

    /**
     * 将参数转换为原始类型。
     *
     * @return 作为原始类型的参数
     */
    Primitive asPrimitive();

    /**
     * 检查参数是否实现为{@link JsonArray}。
     *
     * @return 如果参数是JsonArray则返回true，否则返回false
     */
    boolean isArray();

    /**
     * 检查参数是否实现为ObjectParam。
     *
     * @return 如果参数是ObjectParam则返回true，否则返回false
     */
    boolean isObject();

    /**
     * 将参数转换为JsonArray类型。
     *
     * @return 作为JsonArray的参数
     */
    JsonArray asArray();

    /**
     * 将参数转换为JsonObject类型。
     *
     * @return 作为JsonObject的参数
     */
    JsonObject asObject();

    /**
     * 设置当前节点的父节点。
     *
     * @param parentNode 要设置的父节点
     */
    void setParentNode(JsonSchema parentNode);

    /**
     * 获取当前节点的父节点。
     *
     * @return 父节点
     */
    JsonSchema getParentNode();

    /**
     * 检查当前节点是否为根节点。
     *
     * @return 如果是根节点则返回true，否则返回false
     */
    boolean isRootNode();

    /**
     * 检查参数是否为ObjectNode值类型（例如，Array[{ObjectNode},{ObjectNode}]）。
     *
     * @return 如果是ObjectNode值类型则返回true，否则返回false
     */
    boolean isObjectValue();

    /**
     * 获取参数的数据类型。
     *
     * @return 数据类型
     */
    DataType getDataType();

    /**
     * 检查参数是否为必需的。
     *
     * @return 如果参数是必需的则返回true，否则返回false
     */
    boolean isRequired();

    /**
     * 获取参数的路径。
     *
     * @return 路径作为字符串
     */
    String getPath();

    /**
     * 对给定的JSON节点执行自定义验证。
     *
     * @param jsonNode 要验证的JSON节点
     * @throws ValidationException 如果验证失败
     */
    void verify(JsonNode jsonNode) throws ValidationException;

    /**
     * 深度克隆Schema
     * @return
     */
    JsonSchema clone();
}
