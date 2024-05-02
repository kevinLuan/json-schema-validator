package cn.taskflow.jcv.extension;

import java.util.List;

import cn.taskflow.jcv.core.JsonSchema;
import cn.taskflow.jcv.core.JsonArray;
import cn.taskflow.jcv.core.JsonObject;

/**
 * 调整参数实例对象，在通过反序列化框架序列化出来的对象统一是ParamBase类型，调整后会改为原始类型
 * 
 * @author KEVIN LUAN
 */
public class AdjustParamInstance {

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
				throw new IllegalArgumentException("Unsupported type: " + jsonSchema);
			}
		}
	}

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
			throw new IllegalArgumentException("Unsupported type: " + jsonSchema);
		}
		return refJsonSchema;
	}

	private static void refreshChildrens(JsonSchema children) {
		if (children.isArray()) {
			JsonArray array = children.asArray();
			refreshChildrens(array.getChildren());
		} else if (children.isObject()) {
			JsonObject object = children.asObject();
			refreshChildrens(object.getChildren());
		}
	}

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
					throw new IllegalArgumentException("Unsupported type: " + childrens[i]);
				}
			}
		}
	}
}