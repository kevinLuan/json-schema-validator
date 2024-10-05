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
 * 参数定义
 *
 * @author KEVIN LUAN
 */
public interface JsonSchema {
    Logger LOG = LoggerFactory.getLogger(JsonSchema.class);

    /**
     * 获取参数名称
     *
     * @return
     */
    String getName();

    /**
     * 基础原子参数类型
     *
     * @return
     */
    boolean isPrimitive();

    /**
     * 类型转化
     *
     * @return
     */
    Primitive asPrimitive();

    /**
     * 验证是否是{@link JsonArray}类型实现
     *
     * @return
     */
    boolean isArray();

    /**
     * 验证是否是ObjectParam类型实现
     *
     * @return
     */
    boolean isObject();

    /**
     * 类型转化
     *
     * @return
     */
    JsonArray asArray();

    /**
     * 类型转化
     *
     * @return
     */
    JsonObject asObject();

    /**
     * 设置当前节点的父亲节点
     *
     * @param parentNode
     */
    void setParentNode(JsonSchema parentNode);

    /**
     * 获取父亲节点
     *
     * @return
     */
    public JsonSchema getParentNode();

    /**
     * 跟节点
     *
     * @return
     */
    public boolean isRootNode();

    /**
     * 是ObjectNode值类型(如：Array[{ObjectNode},{ObjectNode}])
     *
     * @return
     */
    boolean isObjectValue();

    /**
     * 获取数据类型
     *
     * @return
     */
    DataType getDataType();

    /**
     * 参数是否为必须的
     *
     * @return
     */
    boolean isRequired();

    String getPath();

    /**
     * 自定义验证
     *
     * @param jsonNode
     */
    void verify(JsonNode jsonNode) throws ValidationException;
}
