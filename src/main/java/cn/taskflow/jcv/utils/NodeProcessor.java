package cn.taskflow.jcv.utils;

import cn.taskflow.jcv.core.JsonSchema;

@FunctionalInterface
public interface NodeProcessor {
    /**
     * 递归每个参数节点调用该执行器
     *
     * @param jsonSchema
     */
    void process(JsonSchema jsonSchema);
}