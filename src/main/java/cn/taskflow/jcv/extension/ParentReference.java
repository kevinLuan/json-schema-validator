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
import java.util.Optional;

import cn.taskflow.jcv.core.JsonSchema;
import cn.taskflow.jcv.core.JsonArray;
import cn.taskflow.jcv.core.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for setting parent node references for JSON schema objects.
 * It provides methods to refresh parent references for both lists and arrays of JSON schemas.
 * 
 * 对象父级节点引用设置
 *
 * @author KEVIN LUAN
 */
public class ParentReference {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParentReference.class);

    /**
     * Refreshes the parent references for a list of JSON schemas.
     * It iterates over each schema and determines if it is an array or an object,
     * then calls the appropriate method to set the parent reference.
     *
     * @param jsonSchemas List of JSON schemas to refresh parent references for.
     */
    public static void refreshParentReference(List<JsonSchema> jsonSchemas) {
        for (JsonSchema pm : jsonSchemas) {
            if (pm.isArray()) {
                arrayParam(pm.asArray(), null);
            } else if (pm.isObject()) {
                objectParam(pm.asObject(), null);
            }
        }
    }

    /**
     * Refreshes the parent references for a variable number of JSON schemas.
     * Similar to the list version, it checks each schema type and sets the parent reference accordingly.
     *
     * 设置Param父级节点引用
     *
     * @param jsonSchemas Varargs of JSON schemas to refresh parent references for.
     */
    public static void refreshParentReference(JsonSchema... jsonSchemas) {
        for (JsonSchema pm : jsonSchemas) {
            if (pm.isArray()) {
                arrayParam(pm.asArray(), null);
            } else if (pm.isObject()) {
                objectParam(pm.asObject(), null);
            }
        }
    }

    /**
     * Sets the parent node for a JSON array and recursively sets parent nodes for its children.
     * It checks if the array has children and processes the first child schema to set its parent.
     *
     * @param array  The JSON array whose parent node is to be set.
     * @param parent The parent JSON schema to be set as the parent node.
     */
    private static void arrayParam(JsonArray array, JsonSchema parent) {
        array.setParentNode(parent);
        if (array.existsChildren()) {
            Optional<JsonSchema> optional = array.getSchemaForFirstChildren();
            if (optional.isPresent()) {
                JsonSchema jsonSchema = optional.get();
                if (jsonSchema.isObject()) {
                    objectParam(jsonSchema.asObject(), array);
                } else if (jsonSchema.isPrimitive()) {
                    jsonSchema.setParentNode(array);
                } else if (jsonSchema.isArray()) {
                    arrayParam(jsonSchema.asArray(), array);
                } else {
                    LOGGER.warn("There is no parent reference type -> " + jsonSchema);
                }
            }
        }
    }

    /**
     * Sets the parent node for a JSON object and recursively sets parent nodes for its children.
     * It iterates over each child schema and sets the parent node based on its type.
     *
     * @param object The JSON object whose parent node is to be set.
     * @param parent The parent JSON schema to be set as the parent node.
     */
    private static void objectParam(JsonObject object, JsonSchema parent) {
        object.setParentNode(parent);
        if (object.existsChildren()) {
            for (JsonSchema pm : object.getChildren()) {
                if (pm.isObject()) {
                    objectParam(pm.asObject(), object);
                } else if (pm.isPrimitive()) {
                    pm.setParentNode(object);
                } else if (pm.isArray()) {
                    arrayParam(pm.asArray(), object);
                } else {
                    LOGGER.warn("There is no parent reference type -> " + pm);
                }
            }
        }
    }
}
