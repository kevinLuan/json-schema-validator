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
package cn.taskflow.jcv.extension;

import java.util.List;

import cn.taskflow.jcv.core.JsonSchema;
import cn.taskflow.jcv.core.JsonArray;
import cn.taskflow.jcv.core.JsonObject;
import cn.taskflow.jcv.exception.ValidationException;

/**
 * Adjusts parameter instance objects. When objects are deserialized using a framework,
 * they are uniformly of type ParamBase. After adjustment, they are converted to their original types.
 * This class provides methods to adjust JsonSchema objects to their specific types.
 *
 * @author KEVIN LUAN
 */
public class AdjustParamInstance {

    /**
     * Adjusts a list of JsonSchema objects to their specific types.
     * If a JsonSchema is an array, it is converted to a JsonArray.
     * If it is an object, it is converted to a JsonObject.
     * If it is a primitive, it remains as a primitive.
     * Throws a ValidationException if the type is unsupported.
     *
     * @param jsonSchemaList the list of JsonSchema objects to adjust
     */
    public static void adjust(List<JsonSchema> jsonSchemaList) {
        for (int i = 0; i < jsonSchemaList.size(); i++) {
            JsonSchema jsonSchema = jsonSchemaList.get(i);
            if (jsonSchema.isArray()) {
                jsonSchemaList.set(i, jsonSchema.asArray());
                jsonSchema = jsonSchemaList.get(i);
                refreshChildrens(jsonSchema.asArray().getChildren());
            } else if (jsonSchema.isObject()) {
                jsonSchemaList.set(i, jsonSchema.asObject());
                jsonSchema = jsonSchemaList.get(i);
                refreshChildrens(jsonSchema.asObject().getChildren());
            } else if (jsonSchema.isPrimitive()) {
                jsonSchemaList.set(i, jsonSchema.asPrimitive());
            } else {
                throw new ValidationException("Unsupported type: " + jsonSchema, jsonSchema.getPath());
            }
        }
    }

    /**
     * Adjusts a single JsonSchema object to its specific type.
     * Converts the JsonSchema to a JsonArray, JsonObject, or primitive based on its type.
     * Throws a ValidationException if the type is unsupported.
     *
     * @param jsonSchema the JsonSchema object to adjust
     * @return the adjusted JsonSchema object
     */
    public static JsonSchema adjust(JsonSchema jsonSchema) {
        JsonSchema refJsonSchema = jsonSchema;
        if (jsonSchema.isArray()) {
            refJsonSchema = jsonSchema.asArray();
            refreshChildrens(jsonSchema.asArray().getChildren());
        } else if (jsonSchema.isObject()) {
            refJsonSchema = jsonSchema.asObject();
            refreshChildrens(jsonSchema.asObject().getChildren());
        } else if (jsonSchema.isPrimitive()) {
            refJsonSchema = jsonSchema.asPrimitive();
        } else {
            throw new ValidationException("Unsupported type: " + jsonSchema, jsonSchema.getPath());
        }
        return refJsonSchema;
    }

    /**
     * Recursively refreshes the children of a JsonSchema object.
     * If the children are arrays or objects, it continues to refresh their children.
     *
     * @param children the JsonSchema object whose children need to be refreshed
     */
    private static void refreshChildrens(JsonSchema children) {
        if (children.isArray()) {
            JsonArray array = children.asArray();
            refreshChildrens(array.getChildren());
        } else if (children.isObject()) {
            JsonObject object = children.asObject();
            refreshChildrens(object.getChildren());
        }
    }

    /**
     * Recursively refreshes an array of JsonSchema objects.
     * Converts each JsonSchema to its specific type and refreshes its children if necessary.
     * Throws a ValidationException if a type is unsupported.
     *
     * @param childrens the array of JsonSchema objects to refresh
     */
    private static void refreshChildrens(JsonSchema[] childrens) {
        if (childrens != null) {
            for (int i = 0; i < childrens.length; i++) {
                if (childrens[i].isArray()) {
                    childrens[i] = childrens[i].asArray();
                    refreshChildrens(childrens[i]);
                } else if (childrens[i].isObject()) {
                    childrens[i] = childrens[i].asObject();
                    refreshChildrens(childrens[i]);
                } else if (childrens[i].isPrimitive()) {
                    childrens[i] = childrens[i].asPrimitive();
                } else {
                    throw new ValidationException("Unsupported type: " + childrens[i], childrens[i].getPath());
                }
            }
        }
    }
}