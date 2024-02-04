package io.github.jcv.core;

import io.github.jcv.domain.api.DataResult;
import io.github.jcv.json.api.JsonUtils;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import io.github.jcv.ext.ApiCheck;
import io.github.jcv.ext.ApiParams;
import io.github.jcv.ext.ApiResponse;
import io.github.jcv.ext.UnknownNodeFilter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.JsonNode;

public class Validator {
	private ApiCheck<HttpServletRequest> requestCheck;
	private ApiCheck<JsonNode> responseCheck;

	public static Validator make(JsonSchema[] request, JsonSchema response) {
		Validator helper = new Validator();
		helper.responseCheck = ApiResponse.make(response);
		helper.requestCheck = ApiParams.make(request);
		return helper;
	}

	/**
	 * 设置未知Node节点过滤器
	 * 
	 * @param filter
	 */
	public Validator setUnknownNodeFilter(UnknownNodeFilter filter) {
		if (this.requestCheck != null) {
			this.requestCheck.setUnknownNodeFilter(filter);
		}
		if (this.responseCheck != null) {
			this.responseCheck.setUnknownNodeFilter(filter);
		}
		return this;
	}

	public static Validator request(JsonSchema... jsonSchemas) {
		Validator helper = new Validator();
		helper.requestCheck = ApiParams.make(jsonSchemas);
		return helper;
	}

	public static Validator response(JsonSchema response) {
		Validator helper = new Validator();
		helper.responseCheck = ApiResponse.make(response);
		return helper;
	}

	public Validator checkRequest(HttpServletRequest request) {
		requestCheck.check(request);
		return this;
	}

	/**
	 * 提取参数数据
	 * 
	 * @param request
	 * @return
	 */
	public Map<String, Object> extractRequest(HttpServletRequest request) {
		return requestCheck.extract(request);
	}

	public Validator checkResponse(JsonNode jsonNode) {
		responseCheck.check(jsonNode);
		return this;
	}

	public Validator checkResponse(DataResult<?> dataResult) {
		JsonNode jsonNode = JsonUtils.parser(dataResult.toJSON());
		responseCheck.check(jsonNode);
		return this;
	}

	/**
	 * 提取数据
	 * @return
	 */
	public Map<String, Object> extractResponse(JsonNode json) {
		return responseCheck.extract(json);
	}

	public MultiValueMap<String, String> asMultiValueMap(Map<String, Object> params) {
		MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<String, String>(params.size());
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			paramMap.add(entry.getKey(), entry.getValue().toString());
		}
		return paramMap;
	}

}
