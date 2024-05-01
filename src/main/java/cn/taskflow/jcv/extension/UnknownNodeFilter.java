package cn.taskflow.jcv.extension;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface UnknownNodeFilter {
    /**
     * 处理未知的Node节点
     *
     * @param name   未知的node name
     * @param parent
     */
    void process(String name, ObjectNode parent);
}
