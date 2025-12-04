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

import cn.taskflow.jcv.exception.ValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;

public class JsonHelper {

    private String              json     = null;
    private JsonNode            jsonNode;
    private boolean             failover = true;
    private static ObjectMapper mapper   = new ObjectMapper();

    public JsonHelper(String json) throws IOException {
        this.json = json;
        this.jsonNode = mapper.readTree(json);
    }

    public JsonHelper(JsonNode jsonNode) {
        Objects.requireNonNull(jsonNode, "`jsonNode` Parameter cannot be null");
        this.jsonNode = jsonNode;
        this.json = jsonNode.toString();
    }

    public static JsonHelper of(String json) throws IOException {
        return new JsonHelper(json);
    }

    public static JsonHelper of(JsonNode node) {
        return new JsonHelper(node);
    }

    /**
     * 深度遍历
     *
     * @param path
     */
    public void deepTraversal(String path, IteratorFunc func) {
        String[] nodes = parsePath(path);
        if (this.jsonNode.isObject()) {
            doDeepSearch(path, (ObjectNode) this.jsonNode, nodes, null, null, Option.traversal, func);
        } else if (this.jsonNode.isArray()) {
            doDeepSearch(path, (ArrayNode) this.jsonNode, nodes, null, null, Option.traversal, func);
        } else {
            throw new ValidationException("Not supported: `" + path + "` node", path);
        }
    }

    /**
     * 如果path叶子节点缺失则添加叶子节点
     *
     * @param path
     * @param value
     */
    public void missingAndSet(String path, String value) {
        String[] nodes = parsePath(path);
        if (this.jsonNode.isObject()) {
            deepSearch(path, (ObjectNode) this.jsonNode, nodes, null, value, Option.missingAndAdd);
        } else if (this.jsonNode.isArray()) {
            deepSearch(path, (ArrayNode) this.jsonNode, nodes, null, value, Option.missingAndAdd);
        } else {
            throw new ValidationException("Not supported: `" + path + "` node", path);
        }
    }

    /**
     * 根据Path节点获取Value
     *
     * @param path
     * @return
     */
    public JsonNode get(String path) {
        String[] nodes = parsePath(path);
        JsonNode root = this.jsonNode;
        if (root.isObject()) {
            for (int i = 0; i < nodes.length - 1; i++) {
                root = root.get(nodes[i]);
                if (root == null) {
                    if (failover) {
                        return null;
                    } else {
                        throw new ValidationException("invalid node:`" + nodes[i] + "` path: `" + path + "`", path);
                    }
                }
            }
            if (root.isObject()) {
                return root.get(nodes[nodes.length - 1]);
            }
        }
        throw new ValidationException("Not supported operator: `" + path + "` node", path);
    }

    /**
     * 根据json node path删除
     *
     * @param path
     */
    public void delete(String path) {
        String[] nodes = parsePath(path);
        if (this.jsonNode.isObject()) {
            deepSearch(path, (ObjectNode) this.jsonNode, nodes, null, null, Option.delete);
        } else if (this.jsonNode.isArray()) {
            deepSearch(path, (ArrayNode) this.jsonNode, nodes, null, null, Option.delete);
        } else {
            throw new ValidationException("Not supported: `" + path + "` node", path);
        }
    }

    /**
     * 比较并删除
     *
     * @param path
     * @param expect
     */
    public void compareAndDelete(String path, String expect) {
        String[] nodes = parsePath(path);
        if (this.jsonNode.isObject()) {
            deepSearch(path, (ObjectNode) this.jsonNode, nodes, expect, null, Option.compareAndDel);
        } else if (this.jsonNode.isArray()) {
            deepSearch(path, (ArrayNode) this.jsonNode, nodes, expect, null, Option.compareAndDel);
        } else {
            throw new ValidationException("Not supported: `" + path + "` node", path);
        }
    }

    /**
     * 验证path节点value是否与期望值相等
     *
     * @param path
     * @param expect
     * @return
     */
    public boolean checkValue(String path, String expect) {
        return checkValue(jsonNode, path, expect);
    }

    private boolean checkValue(JsonNode root, String path, String expect) {
        String[] nodes = parsePath(path);
        for (int i = 0; i < nodes.length; i++) {
            root = root.get(nodes[i]);
            if (root == null && i != nodes.length - 1) {
                return false;
            }
        }
        if (isNull(root) || expect == null) {
            return isNull(root) && expect == null;
        } else {
            return root.textValue().equals(expect);
        }
    }

    private boolean isNull(JsonNode value) {
        return value == null || value.isNull() || value.isMissingNode();
    }

    /**
     * 进入指定path节点,返回path节点下的对象引用
     *
     * @param path
     * @return
     */
    public JsonHelper cd(String path) {
        String[] nodes = parsePath(path);
        JsonNode root = jsonNode;
        if (!root.isObject()) {
            throw new ValidationException("invalid nodeType:`" + root.getNodeType() + "`", path);
        }
        for (int i = 0; i < nodes.length - 1; i++) {
            root = root.get(nodes[i]);
            if (root == null || !root.isObject()) {
                throw new ValidationException("invalid path:`" + path + "`", path);
            }
        }
        root = root.get(nodes[nodes.length - 1]);
        if (root == null) {
            throw new ValidationException("invalid path:`" + path + "`", path);
        }
        return JsonHelper.of(root);
    }

    /**
     * 根据path节点比较期望目标值比较，如果相等，则使用新的值覆盖
     *
     * @param path
     * @param expect
     * @param update
     */
    public void compareAndSet(String path, String expect, String update) {
        String[] nodes = parsePath(path);
        if (this.jsonNode.isObject()) {
            deepSearch(path, (ObjectNode) this.jsonNode, nodes, expect, update, Option.compareAndSet);
        } else if (this.jsonNode.isArray()) {
            deepSearch(path, (ArrayNode) this.jsonNode, nodes, expect, update, Option.compareAndSet);
        } else {
            throw new ValidationException("Not supported: `" + path + "` node", path);
        }
    }

    private void deepSearch(String path, ObjectNode objectNode, String[] nodes, String expect, String update,
                            Option option) {
        doDeepSearch(path, objectNode, nodes, expect, update, option, null);
    }

    private void doDeepSearch(String path, ObjectNode objectNode, String[] nodes, String expect, String update,
                              Option option, IteratorFunc func) {
        JsonNode root = objectNode;
        for (int i = 0; i < nodes.length - 1; i++) {
            root = root.get(nodes[i]);
            if (root == null) {
                if (failover) {
                    return;
                } else {
                    throw new ValidationException("invalid node: `" + nodes[i] + "` path:" + path, path);
                }
            } else if (root.isObject()) {
                continue;
            } else if (root.isArray()) {
                String array[] = new String[nodes.length - i - 1];
                System.arraycopy(nodes, i + 1, array, 0, array.length);
                doDeepSearch(path, (ArrayNode) root, array, expect, update, option, func);
                return;
            } else {
                throw new ValidationException("invalid node: `" + nodes[i] + "` path:" + path, path);
            }
        }
        String name = nodes[nodes.length - 1];
        nodeOperator(name, expect, update, root, option, func);
    }

    private void nodeOperator(String name, String expect, String update, JsonNode root, Option option, IteratorFunc func) {
        if (root.isObject()) {
            ObjectNode objNode = (ObjectNode) root;
            process(name, expect, update, option, func, objNode);
        } else if (root.isArray()) {
            for (int i = 0; i < root.size(); i++) {
                if (root.get(i).isObject()) {
                    ObjectNode objNode = (ObjectNode) root.get(i);
                    process(name, expect, update, option, func, objNode);
                } else {
                    throw new IllegalArgumentException("invalid node");
                }
            }
        } else {
            throw new IllegalArgumentException("invalid node");
        }
    }

    private void process(String name, String expect, String update, Option option, IteratorFunc func, ObjectNode objNode) {
        if (option == Option.compareAndSet) {
            if (checkValueEq(objNode, name, expect)) {
                objNode.put(name, update);
            }
        } else if (option == Option.set) {
            objNode.put(name, update);
        } else if (option == Option.delete) {
            objNode.remove(name);
        } else if (option == Option.compareAndDel) {
            if (checkValueEq(objNode, name, expect)) {
                objNode.remove(name);
            }
        } else if (option == Option.missingAndAdd) {
            JsonNode value = objNode.get(name);
            if (value == null || value.isMissingNode()) {
                objNode.put(name, update);
            }
        } else if (option == Option.traversal) {
            JsonNode value = objNode.get(name);
            if (value != null) {
                func.run(value);
            }
        } else {
            throw new IllegalArgumentException("option:" + option);
        }
    }

    /**
     * 验证节点.name 到值与期望值相等
     *
     * @param objectNode
     * @param name
     * @param expect
     * @return
     */
    private boolean checkValueEq(ObjectNode objectNode, String name, String expect) {
        JsonNode value = objectNode.get(name);
        if (isNull(value) || expect == null) {
            return isNull(value) && expect == null;
        } else if (value.isValueNode()) {
            return value.textValue().equals(expect);
        } else {
            return false;
        }
    }

    private void deepSearch(String path, ArrayNode arrayNode, String[] nodes, String expect, String update,
                            Option option) {
        doDeepSearch(path, arrayNode, nodes, expect, update, option, null);
    }

    private void doDeepSearch(String path, ArrayNode arrayNode, String[] nodes, String expect, String update,
                              Option option, IteratorFunc func) {
        for (int i = 0; i < arrayNode.size(); i++) {
            if (arrayNode.get(i).isObject()) {
                doDeepSearch(path, (ObjectNode) arrayNode.get(i), nodes, expect, update, option, func);
            } else {
                throw new IllegalArgumentException("invalid node");
            }
        }
    }

    /**
     * 遍历ArrayNode节点
     */
    public void foreach(IteratorFunc func) {
        if (this.jsonNode != null && this.jsonNode.isArray()) {
            for (int i = 0; i < jsonNode.size(); i++) {
                func.run(jsonNode.get(i));
            }
        } else {
            throw new IllegalArgumentException("The foreach operation is only supported under the ArrayNode node :`"
                                               + this.jsonNode + "`");
        }
    }

    @FunctionalInterface
    public interface IteratorFunc {

        void run(JsonNode node);

    }

    /**
     * 根据path设置value
     *
     * @param path
     * @param value
     */
    public void set(String path, String value) {
        String[] nodes = parsePath(path);
        if (this.jsonNode.isObject()) {
            deepSearch(path, (ObjectNode) this.jsonNode, nodes, null, value, Option.set);
        } else if (this.jsonNode.isArray()) {
            deepSearch(path, (ArrayNode) this.jsonNode, nodes, null, value, Option.set);
        } else {
            throw new ValidationException("Not supported path: `" + path + "` node", path);
        }
    }

    private String[] parsePath(String path) {
        if (path.indexOf(".") != -1) {
            return path.split("\\.");
        } else {
            return new String[] { path };
        }
    }

    public JsonNode getJsonNode() {
        return this.jsonNode;
    }

    public enum Option {
        compareAndSet, set, delete, compareAndDel, missingAndAdd, traversal
    }

}
