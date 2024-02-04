package io.github.jcv.ext;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.jcv.core.JsonSchema;
import io.github.jcv.core.JsonArray;
import io.github.jcv.core.JsonObject;

abstract class ApiBase<P> implements ApiCheck<P> {
	private UnknownNodeFilter filter;

	public void setUnknownNodeFilter(UnknownNodeFilter filter) {
		this.filter = filter;
	}

	/**
	 * 设置引用
	 * 
	 * @return
	 */
	public abstract ApiBase<P> setReference();

	public abstract String getTipError(String path);

	public abstract String getTipMissing(String path);

	void object(JsonNode node, JsonSchema jsonSchema) {
		if (!node.isObject() || !jsonSchema.isObject()) {
			throw new IllegalArgumentException(getTipError(jsonSchema.getPath()));
		}
		ObjectNode objectNode = (ObjectNode) node;
		JsonObject paramObject = jsonSchema.asObject();
		if (!paramObject.existsChildren()) {
			return;// 没有配置子节点的话，当前节点下的任意子节点均保留
		}
		JsonSchema childrens[] = paramObject.getChildren();
		delete(objectNode, childrens);
		for (int i = 0; i < childrens.length; i++) {
			JsonSchema children = childrens[i];
			JsonNode jsonNode = objectNode.get(children.getName());
			if (!isEmptyNode(jsonNode)) {
				if (children.isObject()) {
					object(jsonNode, children);
				} else if (children.isArray()) {
					array(jsonNode, children);
				}
			}
		}
	}

	/**
	 * 删除不存在的Node
	 * 
	 * @param objectNode
	 * @param childrens
	 */
	void delete(ObjectNode objectNode, JsonSchema childrens[]) {
		Iterator<String> iterator = objectNode.fieldNames();
		List<String> deletes = new LinkedList<>();
		while (iterator.hasNext()) {
			String name = iterator.next();
			if (!exists(childrens, name)) {
				deletes.add(name);
			}
		}
		if (filter != null) {
			for (String key : deletes) {
				filter.process(key, objectNode);
			}
		} else {
			for (String key : deletes) {
				objectNode.remove(key);
			}
		}
	}

	boolean exists(JsonSchema childrens[], String name) {
		for (JsonSchema p : childrens) {
			if (name.equals(p.getName())) {
				return true;
			}
		}
		return false;
	}

	void array(JsonNode jsonNode, JsonSchema jsonSchema) {
		if (!jsonNode.isArray() || !jsonSchema.isArray()) {
			throw new IllegalArgumentException(getTipError(jsonSchema.getPath()));
		}
		JsonArray array = (JsonArray) jsonSchema;
		ArrayNode arrayNode = (ArrayNode) jsonNode;
		if (!array.existsChildren()) {
			return;
		}
		JsonSchema children = array.getChildrenAsParam();
		for (int i = 0; i < arrayNode.size(); i++) {
			JsonNode node = arrayNode.get(i);
			if (node.isObject()) {
				object(node, children);
			}
		}
	}
	
	public boolean isEmptyNode(JsonNode node){
		if(node==null || node.isNull() || node.isMissingNode()){
			return true;
		}
		return false;
	}
}
