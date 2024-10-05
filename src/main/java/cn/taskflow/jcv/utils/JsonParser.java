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
import cn.taskflow.jcv.extension.SchemaOptions;
import cn.taskflow.jcv.extension.SchemaProcess;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 将JSON协议转到到Param
 *
 * @author KEVIN LUAN
 */
public class JsonParser {
    private final static Logger log         = LoggerFactory.getLogger(JsonParser.class);
    public static final String  DESCRIPTION = null;

    /**
     * 根据json数据生成Param验证对象
     *
     * @param json {"name":"IPhone7","price":99.98,"skus":[{"id":100,"name":"移动版","code":[{"id":12345,"title":"土黄金色"}]}]}
     * @return
     */
    public static JsonSchema parseJsonSchema(String json) {
        return parseJsonSchema(json, SchemaOptions.OPTIONAL.getSchemaProcess());
    }

    public static JsonSchema parseJsonSchema(String json, SchemaProcess option) {
        JsonElement element = com.google.gson.JsonParser.parseString(json);
        JsonSchema jsonSchema = null;
        if (element.isJsonArray()) {
            jsonSchema = parserArray("", element.getAsJsonArray(), option);
        } else if (element.isJsonObject()) {
            jsonSchema = parserObject("", element.getAsJsonObject(), option);
        } else if (element.isJsonPrimitive()) {
            jsonSchema = parserPrimitive("", element.getAsJsonPrimitive(), option);
        } else {
            throw new IllegalArgumentException("Not Support type:" + element);
        }
        return jsonSchema;
    }

    private static JsonBasicSchema parserObject(String name, com.google.gson.JsonObject jsonObject, SchemaProcess option) {
        JsonSchema values[] = new JsonSchema[jsonObject.size()];
        Iterator<Map.Entry<String, JsonElement>> iterator = jsonObject.entrySet().iterator();
        int index = 0;
        while (iterator.hasNext()) {
            Entry<String, JsonElement> entry = iterator.next();
            String key = entry.getKey();
            JsonElement val = entry.getValue();
            if (val.isJsonObject()) {
                values[index] = parserObject(key, val.getAsJsonObject(), option);
            } else if (val.isJsonArray()) {
                values[index] = parserArray(key, val.getAsJsonArray(), option);
            } else if (val.isJsonPrimitive()) {
                values[index] = parserPrimitive(key, val.getAsJsonPrimitive(), option);
            } else if (val.isJsonNull()) {
                values[index] = JsonAny.optional(key, DESCRIPTION);
            } else {
                throw new IllegalArgumentException("Not support key:`" + key + "`,value:`" + val + "`");
            }
            index++;
        }
        if (name.length() == 0) {
            if (option.isRequired(name, DataType.Object, values)) {
                return JsonObject.required(values);
            } else {
                return JsonObject.optional(values);
            }
        } else {
            if (option.isRequired(name, DataType.Object, values)) {
                return JsonObject.required(name, DESCRIPTION, values);
            } else {
                return JsonObject.optional(name, DESCRIPTION, values);
            }
        }
    }

    private static JsonArray parserArray(String name, com.google.gson.JsonArray array, SchemaProcess option) {
        if (array.size() > 0) {
            JsonElement element = array.get(0);
            if (element.isJsonObject()) {
                JsonBasicSchema children = parserObject("", element.getAsJsonObject(), option);
                if (option.isRequired(name, DataType.Array, children)) {
                    return JsonArray.required(name, DESCRIPTION, children);
                } else {
                    return JsonArray.optional(name, DESCRIPTION, children);
                }
            } else if (element.isJsonPrimitive()) {
                Primitive primitive = parserPrimitive("", element.getAsJsonPrimitive(), option);
                if (option.isRequired(name, DataType.Array, primitive)) {
                    return JsonArray.required(name, DESCRIPTION, primitive);
                } else {
                    return JsonArray.optional(name, DESCRIPTION, primitive);
                }
            } else {
                throw new IllegalArgumentException("Not support type: `" + element + "`");
            }
        } else {
            if (option.isRequired(name, DataType.Array)) {
                return JsonArray.required(name, DESCRIPTION);
            } else {
                return JsonArray.optional(name, DESCRIPTION);
            }
        }
    }

    private static Primitive parserPrimitive(String name, JsonPrimitive element, SchemaProcess option) {
        if (element.isNumber()) {
            if (name.length() == 0) {
                if (option.isOptional(name, DataType.Number)) {
                    return JsonNumber.ofNullable().setExampleValue(element.getAsNumber());
                } else {
                    return JsonNumber.make().setExampleValue(element.getAsNumber());
                }
            } else {
                if (option.isOptional(name, DataType.Number)) {
                    return JsonNumber.optional(name, DESCRIPTION).setExampleValue(element.getAsNumber());
                } else {
                    return JsonNumber.required(name, DESCRIPTION).setExampleValue(element.getAsNumber());
                }
            }
        } else if (element.isBoolean() || element.getAsString().equals("true") || element.getAsString().equals("false")) {
            if (name.length() == 0) {
                if (option.isOptional(name, DataType.Boolean)) {
                    return JsonBoolean.ofNullable().setExampleValue(element.getAsBoolean());
                } else {
                    return JsonBoolean.make().setExampleValue(element.getAsBoolean());
                }
            } else {
                if (option.isOptional(name, DataType.Boolean)) {
                    return JsonBoolean.optional(name, DESCRIPTION).setExampleValue(element.getAsBoolean());
                } else {
                    return JsonBoolean.required(name, DESCRIPTION).setExampleValue(element.getAsBoolean());
                }
            }
        } else {
            if (name.length() == 0) {
                if (option.isOptional(name, DataType.String)) {
                    return JsonString.ofNullable().setExampleValue(element.getAsString());
                } else {
                    return JsonString.make().setExampleValue(element.getAsString());
                }
            } else {
                if (option.isOptional(name, DataType.String)) {
                    return JsonString.optional(name, DESCRIPTION).setExampleValue(element.getAsString());
                } else {
                    return JsonString.required(name, DESCRIPTION).setExampleValue(element.getAsString());
                }
            }
        }
    }
}
