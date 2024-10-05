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

import cn.taskflow.jcv.core.JsonArray;
import cn.taskflow.jcv.core.JsonObject;
import cn.taskflow.jcv.core.JsonSchema;
import cn.taskflow.jcv.extension.AdjustParamInstance;
import cn.taskflow.jcv.extension.ParentReference;

public class JsonSchemaProcessor {
    public JsonSchema[] jsonSchemas;

    public JsonSchemaProcessor(JsonSchema... jsonSchemas) {
        this.jsonSchemas = jsonSchemas;
        for (int i = 0; i < this.jsonSchemas.length; i++) {
            JsonSchema p = this.jsonSchemas[i];
            this.jsonSchemas[i] = AdjustParamInstance.adjust(p);
            ParentReference.refreshParentReference(this.jsonSchemas);
        }
    }

    public JsonSchema[] getParams() {
        return jsonSchemas;
    }

    public void run(NodeProcessor nodeProcessor) {
        for (int i = 0; i < jsonSchemas.length; i++) {
            JsonSchema jsonSchema = jsonSchemas[i];
            if (jsonSchema.isArray()) {
                array(nodeProcessor, jsonSchema.asArray());
            } else if (jsonSchema.isObject()) {
                object(nodeProcessor, jsonSchema.asObject());
            } else {
                nodeProcessor.process(jsonSchema);
            }
        }
    }

    private void object(NodeProcessor nodeProcessor, JsonObject obj) {
        nodeProcessor.process(obj);
        JsonSchema[] jsonSchemas = obj.getChildren();
        for (int i = 0; i < jsonSchemas.length; i++) {
            JsonSchema jsonSchema = jsonSchemas[i];
            if (jsonSchema.isObject()) {
                object(nodeProcessor, jsonSchema.asObject());
            } else if (jsonSchema.isArray()) {
                array(nodeProcessor, jsonSchema.asArray());
            } else {
                nodeProcessor.process(jsonSchema);
            }
        }
    }

    private void array(NodeProcessor nodeProcessor, JsonArray array) {
        nodeProcessor.process(array);
        JsonSchema jsonSchema = array.getSchemaForFirstChildren();
        if (jsonSchema.isObject()) {
            object(nodeProcessor, jsonSchema.asObject());
        } else {
            nodeProcessor.process(jsonSchema);
        }
    }
}
