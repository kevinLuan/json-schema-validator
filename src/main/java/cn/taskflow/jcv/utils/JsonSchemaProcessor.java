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
        JsonSchema jsonSchema = array.getChildrenAsParam();
        if (jsonSchema.isObject()) {
            object(nodeProcessor, jsonSchema.asObject());
        } else {
            nodeProcessor.process(jsonSchema);
        }
    }
}
