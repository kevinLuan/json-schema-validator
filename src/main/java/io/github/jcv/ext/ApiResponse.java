package io.github.jcv.ext;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.jcv.core.JsonSchema;

/**
 * API请求响应验证处理
 * 
 * @author KEVIN LUAN
 */
public class ApiResponse extends ApiBase<JsonNode> {
	public JsonSchema responseJsonSchema;
	private CheckParam checkParam = CheckParam.getInstance(this);

	private ApiResponse(JsonSchema responseJsonSchema) {
		this.responseJsonSchema = responseJsonSchema;
	}

	@Override
	public ApiResponse setReference() {
		responseJsonSchema = AdjustParamInstance.adjust(responseJsonSchema);
		ParentReference.refreshParentReference(responseJsonSchema);
		return this;
	}

	/**
	 * 根据API返回协议格式定义进行合法性验证
	 * 
	 * @param jsonNode
	 * @return
	 */
	public ApiResponse check(JsonNode jsonNode) {
		checkParam.checkResponse(responseJsonSchema, jsonNode);
		return this;
	}

	/**
	 * 根据返回数据格式定义extract数据
	 * 
	 * @return
	 */
	public Map<String, Object> extract(JsonNode jsonNode) {
		if (responseJsonSchema == null) {
			throw new IllegalArgumentException("responseParam must be not null");
		}
		object(jsonNode, responseJsonSchema);
		Iterator<String> iterator = jsonNode.fieldNames();
		Map<String, Object> data = new HashMap<>(responseJsonSchema.asObject().getChildren().length);
		while (iterator.hasNext()) {
			String key = iterator.next();
			data.put(key, jsonNode.get(key));
		}
		return data;
	}

	public static ApiCheck<JsonNode> make(JsonSchema jsonSchema) {
		return new ApiResponse(jsonSchema).setReference();
	}

	@Override
	public String getTipError(String path) {
		return "下游服务返回数据错误->`" + path + "`";
	}

	@Override
	public String getTipMissing(String path) {
		return "下游服务返回数据缺失->`" + path + "`";
	}
}
