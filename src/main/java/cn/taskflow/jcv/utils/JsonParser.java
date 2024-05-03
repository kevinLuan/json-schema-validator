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
package cn.taskflow.jcv.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import cn.taskflow.jcv.core.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * 将JSON协议转到到Param
 *
 * @author KEVIN LUAN
 */
public class JsonParser {
    public static final String DESCRIPTION = null;

    /**
     * 根据json数据生成Param验证对象
     *
     * @param json {"name":"IPhone7","price":99.98,"skus":[{"id":100,"name":"移动版","code":[{"id":12345,"title":"土黄金色"}]}]}
     * @return
     */
    public static JsonSchema parseJsonSchema(String json) {
        JsonElement element = com.google.gson.JsonParser.parseString(json);
        JsonSchema jsonSchema = null;
        if (element.isJsonArray()) {
            jsonSchema = parserArray("", element.getAsJsonArray());
        } else if (element.isJsonObject()) {
            jsonSchema = parserObject("", element.getAsJsonObject());
        } else if (element.isJsonPrimitive()) {
            jsonSchema = parserPrimitive("", element.getAsJsonPrimitive());
        } else {
            System.out.println("不支持的类型->" + element);
        }
        return jsonSchema;
    }

    private static JsonBasicSchema parserObject(String name, com.google.gson.JsonObject jsonObject) {
        JsonSchema values[] = new JsonSchema[jsonObject.size()];
        Iterator<Map.Entry<String, JsonElement>> iterator = jsonObject.entrySet().iterator();
        int index = 0;
        while (iterator.hasNext()) {
            Entry<String, JsonElement> entry = iterator.next();
            String key = entry.getKey();
            JsonElement val = entry.getValue();
            if (val.isJsonObject()) {
                values[index] = parserObject(key, val.getAsJsonObject());
            } else if (val.isJsonArray()) {
                values[index] = parserArray(key, val.getAsJsonArray());
            } else if (val.isJsonPrimitive()) {
                values[index] = parserPrimitive(key, val.getAsJsonPrimitive());
            } else {
                System.out.println("不支持的类型 key:" + key + "->" + val);
            }
            index++;
        }
        if (name.length() == 0) {
            return JsonObject.optional(values);
        } else {
            return JsonObject.optional(name, DESCRIPTION, values);
        }
    }

    private static JsonArray parserArray(String name, com.google.gson.JsonArray array) {
        if (array.size() > 0) {
            JsonElement element = array.get(0);
            if (element.isJsonObject()) {
                return JsonArray.optional(name, DESCRIPTION, parserObject("", element.getAsJsonObject()));
            } else if (element.isJsonPrimitive()) {
                return JsonArray.optional(name, DESCRIPTION, parserPrimitive("", element.getAsJsonPrimitive()));
            } else {
                throw new IllegalArgumentException("不支持的类型:" + element);
            }
        }
        return JsonArray.optional(name, DESCRIPTION);
    }

    private static Primitive parserPrimitive(String name, JsonPrimitive element) {
        if (element.isNumber()) {
            if (name.length() == 0) {
                return JsonNumber.ofNullable().setExampleValue(element.getAsNumber());
            } else {
                return JsonNumber.optional(name, DESCRIPTION).setExampleValue(element.getAsNumber());
            }
        } else if (element.isBoolean() || element.getAsString().equals("true") || element.getAsString().equals("false")) {
            if (name.length() == 0) {
                return JsonBoolean.ofNullable().setExampleValue(element.getAsBoolean());
            } else {
                return JsonBoolean.optional(name, DESCRIPTION).setExampleValue(element.getAsBoolean());
            }
        } else {
            if (name.length() == 0) {
                return JsonString.ofNullable().setExampleValue(element.getAsString());
            } else {
                return JsonString.optional(name, DESCRIPTION).setExampleValue(element.getAsString());
            }
        }
    }
}
