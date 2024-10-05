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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 对象父级节点引用设置
 * 
 * @author KEVIN LUAN
 */
public class ParentReference {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParentReference.class);

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
     * 设置Param父级节点引用
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

    private static void arrayParam(JsonArray array, JsonSchema parent) {
        array.setParentNode(parent);
        if (array.existsChildren()) {
            JsonSchema pm = array.getSchemaForFirstChildren();
            if (pm.isObject()) {
                objectParam(pm.asObject(), array);
            } else if (pm.isPrimitive()) {
                pm.setParentNode(array);
            } else if (pm.isArray()) {
                arrayParam(pm.asArray(), array);
            } else {
                LOGGER.warn("There is no parent reference type -> " + pm);
            }
        }
    }

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
