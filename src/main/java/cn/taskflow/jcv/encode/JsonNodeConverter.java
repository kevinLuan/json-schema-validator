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
package cn.taskflow.jcv.encode;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.*;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-09-25
 */
public interface JsonNodeConverter {
    boolean isNull(JsonNode jsonNode);

    String toString(Object value);

    String toString(JsonNode node);

    Object parserValue(JsonNode node);

    Map<String, Object> parserMap(ObjectNode node);

    List<Object> parserArrayNode(ArrayNode arrayNode);

    JsonNode parser(String json);

    /**
     * 将Java对象转化到JsonNode
     */
    JsonNode convert(Object value);

    String stringify(Object pojo);

    <T> T parse(String json, Class<T> a);

    <T> T parse(String json, TypeReference<T> a);

    String prettyPrinter(JsonNode jsonNode);
}
