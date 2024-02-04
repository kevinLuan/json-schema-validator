package io.github.jcv.ext;

import io.github.jcv.json.api.JsonUtils;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.jcv.core.JsonSchema;

/**
 * API 请求参数定义验证
 * 
 * @author KEVIN LUAN
 */
public class ApiParams extends ApiBase<HttpServletRequest> {
	public List<JsonSchema> jsonSchemaList = new LinkedList<>();
	private CheckParam checkParam = CheckParam.getInstance(this);

	private ApiParams(JsonSchema... jsonSchemas) {
		for (JsonSchema p : jsonSchemas) {
			jsonSchemaList.add(p);
		}
	}

	public static ApiParams make(JsonSchema... jsonSchemas) {
		return new ApiParams(jsonSchemas).setReference();
	}

	@Override
	public ApiParams setReference() {
		AdjustParamInstance.adjust(jsonSchemaList);
		ParentReference.refreshParentReference(jsonSchemaList);
		return this;
	}



	/**
	 * 请求参数合法性验证
	 * 
	 * @param request
	 * @return
	 */
	@Override
	public ApiParams check(HttpServletRequest request) {
		JsonSchema[] jsonSchemas = new JsonSchema[jsonSchemaList.size()];
		jsonSchemaList.toArray(jsonSchemas);
		checkParam.checkParams(request, jsonSchemas);
		return this;
	}

	/**
	 * 根据参数定义extract参数(只会拷贝定义的参数)
	 * 
	 * @return
	 */
	@Override
	public Map<String, Object> extract(HttpServletRequest request) {
		Map<String, Object> data = new HashMap<>(jsonSchemaList.size());
		for (JsonSchema jsonSchema : jsonSchemaList) {
			String value = request.getParameter(jsonSchema.getName());
			if (value == null) {
				continue;
			}
			if (jsonSchema.isPrimitive()) {
				data.put(jsonSchema.getName(), value);
			} else {
				JsonNode node = JsonUtils.parser(value);
				if (jsonSchema.isArray()) {
					if (node.isArray()) {
						array((ArrayNode) node, jsonSchema.asArray());
					} else {
						throw new IllegalArgumentException(getTipError(jsonSchema.getPath()));
					}

				} else if (jsonSchema.isObject()) {
					object((ObjectNode) node, jsonSchema.asObject());
				} else {
					throw new IllegalArgumentException("不支持的操作" + jsonSchema);
				}
				data.put(jsonSchema.getName(), node);
			}
		}
		return data;
	}

	@Override
	public String getTipError(String path) {
		return "`" + path + "`参数错误";
	}

	@Override
	public String getTipMissing(String path) {
		return "`" + path + "`参数缺失";
	}
}
