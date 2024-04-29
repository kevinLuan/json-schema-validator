package cn.taskflow.jcv.core;

/**
 * 对象ObjectNode参数
 * 
 * @author KEVIN LUAN
 */
public class JsonObject extends JsonBasicSchema {

	JsonObject() {
		super();
	}

	public JsonObject(String name, boolean required, String description, JsonSchema[] children) {
		super(name, required, DataType.Object, description);
		if (children != null) {
			this.children = new JsonBasicSchema[children.length];
			for (int i = 0; i < children.length; i++) {
				JsonSchema jsonSchema = children[i];
				this.children[i] = (JsonBasicSchema) jsonSchema;
				if (jsonSchema.isObjectValue()) {
					throw new IllegalArgumentException("ParamObject子节点Name不能为空");
				}
			}
		}
	}

	public static JsonObject required(String name, String description, JsonSchema... children) {
		return new JsonObject(name, true, description, children);
	}

	public static JsonObject required(JsonSchema... children) {
		return new JsonObject("", true, null, children);
	}

	public static JsonObject optional(JsonSchema... children) {
		return new JsonObject("", false, null, children);
	}

	public static JsonObject optional(String name, String description, JsonSchema... children) {
		return new JsonObject(name, false, description, children);
	}

	public boolean isObject() {
		return true;
	}

	@Override
	public JsonObject asObject() {
		return this;
	}

	public final boolean existsChildren() {
		return this.children != null && children.length > 0;
	}

	public JsonSchema[] getChildren() {
		return children;
	}
}
