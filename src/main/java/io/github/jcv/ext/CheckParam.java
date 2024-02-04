package io.github.jcv.ext;

import io.github.jcv.core.DataType;
import io.github.jcv.core.JsonArray;
import io.github.jcv.json.api.JsonUtils;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.jcv.datatype.NumberParser;
import io.github.jcv.core.JsonSchema;
import io.github.jcv.core.JsonObject;

class CheckParam {
	private ApiBase<?> apiBase;

	private CheckParam(ApiBase<?> apiBase) {
		this.apiBase = apiBase;
	}

	public static CheckParam getInstance(ApiBase<?> apiBase) {
		return new CheckParam(apiBase);
	}

	public String getTipError(String path) {
		return apiBase.getTipError(path);
	}

	public String getTipMissing(String path) {
		return apiBase.getTipMissing(path);
	}

	public void checkResponse(JsonSchema jsonSchema, JsonNode jsonNode) {
		if (jsonSchema == null) {
			throw new IllegalArgumentException("param must be not null");
		}
		if (jsonSchema.isRequired()) {
			if (JsonUtils.isNull(jsonNode)) {
				throw new IllegalArgumentException(getTipMissing(jsonSchema.getPath()));
			}
		} else {// 参数允许为空
			if (JsonUtils.isNull(jsonNode)) {
				return;
			}
		}
		if (jsonSchema.isPrimitive()) {
			checkSimple(jsonSchema, jsonNode);
		} else if (jsonSchema.isArray()) {
			checkArray(jsonSchema, jsonNode);
		} else if (jsonSchema.isObject()) {
			checkObject(jsonSchema, jsonNode);
		} else {
			throw new IllegalArgumentException("不支持的类型:" + jsonSchema);
		}
	}

	public void checkParams(HttpServletRequest request, JsonSchema... jsonSchemas) {
		if (request == null) {
			throw new IllegalArgumentException("request must be not null");
		}
		for (JsonSchema jsonSchema : jsonSchemas) {
			String name = jsonSchema.getName();
			String value = request.getParameter(name);
			if (jsonSchema.isRequired()) {
				if (value == null) {
					throw new IllegalArgumentException(getTipMissing(jsonSchema.getPath()));
				}
			} else {// 参数允许为空
				if (value == null) {
					continue;
				}
			}
			if (jsonSchema.isPrimitive()) {
				checkSimple(jsonSchema, JsonNodeFactory.instance.textNode(value));
			} else {
				JsonNode jsonNode = null;
				try {
					jsonNode = JsonUtils.parser(value);
				} catch (Exception e) {
					throw new IllegalArgumentException(getTipError(jsonSchema.getPath()));
				}
				if (jsonSchema.isArray()) {
					checkArray(jsonSchema, jsonNode);
				} else if (jsonSchema.isObject()) {
					checkObject(jsonSchema, jsonNode);
				} else {
					throw new IllegalArgumentException("不支持的类型:" + jsonSchema);
				}
			}
		}
	}

	void checkArray(JsonSchema jsonSchema, JsonNode value) {
		if (jsonSchema.isArray() && value.isArray()) {
			JsonArray array = jsonSchema.asArray();
			if (!array.existsChildren()) {
				return;// 没有子节点
			}
			JsonSchema children = array.getChildrenAsParam();
			if (array.isRequired()) {
				if (value.size() == 0) {
					throw new IllegalArgumentException(jsonSchema.getPath() + "[]不能为空");
				}
			}
			for (int i = 0; i < value.size(); i++) {
				JsonNode node = value.get(i);
				if (children.isObjectValue()) {
					checkObject(children, (ObjectNode) node);
				} else if (children.isPrimitive()) {
					checkSimple(children.asPrimitive(), node);
				} else {
					throw new IllegalArgumentException("不支持的类型" + children);
				}
			}
		} else {
			throw new IllegalArgumentException(getTipError(jsonSchema.getPath()));
		}
	}

	void checkSimple(JsonSchema jsonSchema, JsonNode node) {
		if (jsonSchema.isPrimitive()) {
			if (node.isObject() || node.isArray()) {
				throw new IllegalArgumentException(getTipError(jsonSchema.getPath()));
			}
			String value = JsonUtils.toString(node);
			if (jsonSchema.getDataType().isNumber()) {
				try {
					NumberParser.parse(value, jsonSchema.isRequired()).check(jsonSchema.asPrimitive());
				} catch (NumberFormatException e) {
					if (jsonSchema.getParentNode() != null) {
						if (jsonSchema.getParentNode().isArray()) {
							if (jsonSchema.asPrimitive().existBetweenCheck()) {
								String msg = jsonSchema.asPrimitive().getTipMsg(jsonSchema.getPath() + "[]");
								throw new IllegalArgumentException(msg);
							} else {
								throw new IllegalArgumentException("`" + jsonSchema.getPath() + "[]`只能包含数字");
							}
						}
					}
					if (jsonSchema.asPrimitive().existBetweenCheck()) {
						String msg = jsonSchema.asPrimitive().getTipMsg(jsonSchema.getPath());
						throw new IllegalArgumentException(msg);
					} else {
						throw new IllegalArgumentException("`" + jsonSchema.getPath() + "`必须是一个数字");
					}

				}
			} else if (jsonSchema.getDataType().isString()) {
				DataType.String.check(jsonSchema.asPrimitive(), value);
			} else {
				throw new IllegalArgumentException("不支持的类型:" + jsonSchema.getDataType());
			}
		}
	}

	void checkObject(JsonSchema jsonSchema, JsonNode jsonNode) {
		if (!jsonSchema.isObject() || !jsonNode.isObject()) {
			throw new IllegalArgumentException(getTipError(jsonSchema.getPath()));
		}
		JsonObject obj = jsonSchema.asObject();
		ObjectNode objNode = (ObjectNode) jsonNode;
		for (JsonSchema p : obj.getChildren()) {
			JsonNode value = objNode.get(p.getName());
			if (p.isRequired()) {
				if (JsonUtils.isNull(value)) {
					throw new IllegalArgumentException(getTipMissing(p.getPath()));
				}
			} else {
				if (JsonUtils.isNull(value)) {
					continue;
				}
			}
			if (p.isObject()) {
				checkObject(p, value);
			} else if (p.isArray()) {
				checkArray(p, value);
			} else if (p.isPrimitive()) {
				checkSimple(p, value);
			} else {
				throw new IllegalArgumentException("不支持的类型:" + p);
			}

		}
	}
}
