package io.github.jcv.utils;

import io.github.jcv.core.JsonSchema;

public class ErrorUtils {

	public static RuntimeException newParamMissing(JsonSchema jsonSchema) {
		if (jsonSchema.isRootNode()) {
			throw new IllegalArgumentException("`" + jsonSchema.getName() + "`参数缺失");
		} else {
			throw new IllegalArgumentException("`" + jsonSchema.getName() + "`参数->`" + jsonSchema.getPath() + "`缺失");
		}

	}

	public static IllegalArgumentException newParamError(JsonSchema jsonSchema) {
		if (jsonSchema.isRootNode()) {
			throw new IllegalArgumentException("`" + jsonSchema.getName() + "`参数错误");
		} else {
			throw new IllegalArgumentException("`" + jsonSchema.getName() + "`参数->`" + jsonSchema.getPath() + "`错误");
		}
	}

	public static IllegalArgumentException newParamError(JsonSchema jsonSchema, String detail) {
		if (detail != null) {
			detail = "(" + detail + ")";
		} else {
			detail = "";
		}
		if (jsonSchema.isRootNode()) {
			throw new IllegalArgumentException("`" + jsonSchema.getName() + "`参数错误" + detail);
		} else {
			throw new IllegalArgumentException("`" + jsonSchema.getName() + "`参数->`" + jsonSchema.getPath() + "`错误" + detail);
		}
	}

	public static IllegalArgumentException newParamValueError(JsonSchema jsonSchema) {
		if (jsonSchema.isRootNode()) {
			throw new IllegalArgumentException("`" + jsonSchema.getName() + "`参数数据元素不能为空");
		} else {
			throw new IllegalArgumentException("`" + jsonSchema.getName() + "`参数->`" + jsonSchema.getPath() + "`数据元素不能为空");
		}

	}

	/**
	 * 构造类转换异常
	 * 
	 * @param src
	 * @param dest
	 * @return
	 */
	public static ClassCastException newClassCastException(Class<?> src, Class<?> dest) {
		throw new ClassCastException(src.getName() + " cannot be cast to " + dest.getName());
	}
}
