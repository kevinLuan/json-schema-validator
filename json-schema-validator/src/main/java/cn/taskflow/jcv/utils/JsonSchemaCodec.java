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

import cn.taskflow.jcv.core.*;
import cn.taskflow.jcv.exception.ValidationException;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import cn.taskflow.jcv.datatype.NumberParser;

/**
 * Param到json数据协议转换(注意不是序列化Param协议格式)
 *
 * @author KEVIN LUAN
 */
public class JsonSchemaCodec {
    public static String toJsonDataExample(JsonSchema jsonSchema) {
        JsonElement element = null;
        if (jsonSchema.isArray()) {
            element = array(jsonSchema.asArray());
        } else if (jsonSchema.isObject()) {
            element = object(jsonSchema.asObject());
        } else if (jsonSchema.isPrimitive()) {
            element = primitive(jsonSchema.asPrimitive());
        } else {
            throw new ValidationException("Unsupported type: " + jsonSchema, jsonSchema.getPath());
        }
        return element.toString();
    }

    private static com.google.gson.JsonObject object(JsonObject object) {
        com.google.gson.JsonObject jsonObject = new com.google.gson.JsonObject();
        JsonSchema[] jsonSchemas = object.getChildren();
        for (int i = 0; i < jsonSchemas.length; i++) {
            JsonSchema jsonSchema = jsonSchemas[i];
            if (jsonSchema.isArray()) {
                com.google.gson.JsonArray value = array(jsonSchema.asArray());
                jsonObject.add(jsonSchema.getName(), value);
            } else if (jsonSchema.isObject()) {
                com.google.gson.JsonObject value = object(jsonSchema.asObject());
                jsonObject.add(jsonSchema.getName(), value);
            } else if (jsonSchema.isPrimitive()) {
                jsonObject.add(jsonSchema.getName(), primitive(jsonSchema.asPrimitive()));
            }
        }
        return jsonObject;
    }

    private static com.google.gson.JsonArray array(JsonArray array) {
        com.google.gson.JsonArray jsonArray = new com.google.gson.JsonArray();
        JsonSchema[] jsonSchemas = array.getChildren();
        for (int i = 0; i < jsonSchemas.length; i++) {
            JsonSchema jsonSchema = jsonSchemas[i];
            if (jsonSchema.isArray()) {
                com.google.gson.JsonArray value = array(jsonSchema.asArray());
                jsonArray.add(value);
            } else if (jsonSchema.isObject()) {
                com.google.gson.JsonObject value = object(jsonSchema.asObject());
                jsonArray.add(value);
            } else if (jsonSchema.isPrimitive()) {
                JsonElement value = primitive(jsonSchema.asPrimitive());
                jsonArray.add(value);
            }
        }
        return jsonArray;
    }

    private static JsonElement primitive(Primitive primitive) {
        if (primitive.getDataType() == DataType.Number) {
            if (primitive.getExampleValue() != null) {
                NumberParser numberParser = NumberParser.parse(primitive.getExampleValue(), false);
                return new JsonPrimitive(numberParser.value);
            }
            return JsonNull.INSTANCE;
        } else {
            if (primitive.getExampleValue() != null) {
                return new JsonPrimitive(primitive.getExampleValue());
            } else {
                return JsonNull.INSTANCE;
            }
        }
    }
}
