package io.github.jcv.utils;

import io.github.jcv.core.JsonSchema;

public interface ParamExec {
	/**
	 * 递归每个参数节点调用该执行器
	 * 
	 * @param jsonSchema
	 */
	public void execute(JsonSchema jsonSchema);
}