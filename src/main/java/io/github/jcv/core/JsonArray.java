package io.github.jcv.core;

import org.apache.commons.lang3.StringUtils;

/**
 * 数组参数类型
 * 
 * @author KEVIN LUAN
 */
public class JsonArray extends JsonBasicSchema {

	public JsonArray() {
	}

	public JsonArray(String name, boolean required, String description, JsonBasicSchema childrens) {
		super(name, required, DataType.Array, description);
		check(childrens);
		if (childrens != null) {
			this.children = new JsonBasicSchema[] { childrens };
		}
	}

	private void check(JsonSchema childrens) {
		if (childrens != null) {
			if (childrens.getDataType() == DataType.Array) {
				throw new IllegalArgumentException("无效的数据格式(数组不应该直接嵌套数组)");
			} else{
				if (StringUtils.isNotBlank(childrens.getName())) {
					throw new IllegalArgumentException("ParamArray节点的子节点不应该存在节点名称");
				}
			}
		}
	}

	/**
	 * 创建一个必须参数
	 * 
	 * @param name
	 * @return
	 */
	public static JsonArray required(String name, String description, JsonBasicSchema childrens) {
		return new JsonArray(name, true, description, childrens);
	}

	/**
	 * 创建一个必须的Array节点，任意类型的子节点
	 * 
	 * @param name
	 * @param description
	 * @return
	 */
	public static JsonArray required(String name, String description) {
		return new JsonArray(name, true, description, null);
	}

	public static JsonArray optional(String name, String description, JsonBasicSchema childrens) {
		return new JsonArray(name, false, description, childrens);
	}

	/**
	 * 创建一个非必须的Array节点，任意类型的子节点
	 * 
	 * @param name
	 * @param description
	 * @return
	 */
	public static JsonArray optional(String name, String description) {
		return new JsonArray(name, false, description, null);
	}

	@Override
	public JsonArray asArray() {
		return this;
	}

	public JsonSchema[] getChildren() {
		return children;
	}
	
	public final JsonSchema getChildrenAsParam() {
		if (children != null && children.length > 0) {
			return children[0];
		}
		return null;
	}

	public final boolean existsChildren() {
		return this.children != null && children.length > 0;
	}
}
