package io.github.jcv.utils;

import io.github.jcv.core.JsonSchema;
import io.github.jcv.core.JsonArray;
import io.github.jcv.core.JsonObject;
import io.github.jcv.ext.AdjustParamInstance;
import io.github.jcv.ext.ParentReference;

public class ParamHelper {
	public JsonSchema[] jsonSchemas;

	public ParamHelper(JsonSchema... jsonSchemas) {
		this.jsonSchemas = jsonSchemas;
		this.init();
	}

	private void init() {
		for (int i = 0; i < this.jsonSchemas.length; i++) {
			JsonSchema p = this.jsonSchemas[i];
			this.jsonSchemas[i] = AdjustParamInstance.adjust(p);
			ParentReference.refreshParentReference(this.jsonSchemas);
		}
	}

	public JsonSchema[] getParams() {
		return jsonSchemas;
	}

	public void exec(ParamExec exec) {
		for (int i = 0; i < jsonSchemas.length; i++) {
			JsonSchema jsonSchema = jsonSchemas[i];
			if (jsonSchema.isArray()) {
				array(exec, jsonSchema.asArray());
			} else if (jsonSchema.isObject()) {
				object(exec, jsonSchema.asObject());
			} else {
				exec.execute(jsonSchema);
			}
		}
	}

	private void object(ParamExec exec, JsonObject obj) {
		exec.execute(obj);
		JsonSchema[] jsonSchemas = obj.getChildren();
		for (int i = 0; i < jsonSchemas.length; i++) {
			JsonSchema jsonSchema = jsonSchemas[i];
			if (jsonSchema.isObject()) {
				object(exec, jsonSchema.asObject());
			} else if (jsonSchema.isArray()) {
				array(exec, jsonSchema.asArray());
			} else {
				exec.execute(jsonSchema);
			}
		}
	}

	private void array(ParamExec exec, JsonArray array) {
		exec.execute(array);
		JsonSchema jsonSchema = array.getChildrenAsParam();
		if (jsonSchema.isObject()) {
			object(exec, jsonSchema.asObject());
		} else {
			exec.execute(jsonSchema);
		}
	}
}
