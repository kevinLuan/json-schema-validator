package io.github.jcv.utils;

import io.github.jcv.core.JsonSchema;
import org.apache.commons.lang3.StringUtils;

/**
 * 解析参数节点Path工具类
 *
 * @author KEVIN LUAN
 */
public class NodeHelper {
    // 指定从root节点到当前节点的path (root->current_node)
    private String path;
    private String rootName;
    private String currentNode;

    public String getCurrentName() {
        return this.currentNode;
    }

    public String getRootName() {
        return this.rootName;
    }

    public String getPath() {
        return this.path;
    }

    public String toString() {
        return "rootName:" + rootName + "\tcurrent_node:" + currentNode + "\tpath:" + path;
    }

    public static NodeHelper parser(JsonSchema pm) {
        NodeHelper helper = new NodeHelper();
        helper.init(pm);
        return helper;
    }

    private void init(JsonSchema jsonSchema) {
        if (jsonSchema.isArray()) {
            if (jsonSchema.getParentNode() != null) {
                init(jsonSchema.getParentNode());
            }
        } else if (jsonSchema.isObjectValue()) {
            if (jsonSchema.getParentNode() != null) {
                init(jsonSchema.getParentNode());
            }
        } else {
            this.currentNode = jsonSchema.getName();
        }

        if (jsonSchema.isRootNode()) {
            this.path = jsonSchema.getName();
            this.rootName = jsonSchema.getName();
        } else {
            StringBuilder nodeBuild = new StringBuilder();
            this.rootName = parserPathAndRootNode(jsonSchema, nodeBuild);
            this.path = nodeBuild.toString();
        }
    }

    private String parserPathAndRootNode(JsonSchema jsonSchema, StringBuilder nodeBuild) {
        if (jsonSchema.isRootNode() && nodeBuild.length() == 0) {
            nodeBuild.append(jsonSchema.getName());
            return jsonSchema.getName();
        }
        if (StringUtils.isNotBlank(jsonSchema.getName())) {
            if (nodeBuild.length() == 0) {
                nodeBuild.append(jsonSchema.getName());
            } else {
                nodeBuild.insert(0, jsonSchema.getName() + ".");
            }
        }
        if (jsonSchema.isRootNode()) {
            return jsonSchema.getName();
        } else {
            return parserPathAndRootNode(jsonSchema.getParentNode(), nodeBuild);
        }
    }

    public boolean isRootNode() {
        return this.rootName.equals(this.path);
    }
}
