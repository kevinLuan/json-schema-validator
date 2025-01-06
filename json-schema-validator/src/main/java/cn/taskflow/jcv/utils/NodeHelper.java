/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.taskflow.jcv.utils;

import cn.taskflow.jcv.core.JsonSchema;

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
