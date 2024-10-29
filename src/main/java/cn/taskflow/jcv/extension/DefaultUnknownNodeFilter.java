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
package cn.taskflow.jcv.extension;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * DefaultUnknownNodeFilter is a class that implements the UnknownNodeFilter interface.
 * It provides a mechanism to process JSON object nodes by filtering out nodes that do not
 * meet specific criteria. This is particularly useful in scenarios where JSON data
 * needs to be validated or cleaned before further processing.
 * 
 * The class uses a singleton pattern, providing a single instance via the INSTANCE field.
 * The process method is the core functionality, which checks if a node should be retained
 * or removed based on its name and type.
 * 
 * The filter supports nodes with "extendProps" and "remark" attributes, allowing them
 * to pass through if they match the required format. Otherwise, the node is removed.
 * 
 * This class is designed to be used in JSON validation frameworks where unknown nodes
 * need to be handled gracefully.
 * 
 * Usage example:
 * DefaultUnknownNodeFilter.INSTANCE.process(nodeName, parentNode);
 * 
 * This will remove the node with the specified name from the parentNode if it does not
 * meet the criteria defined in the ExtendNode definitions.
 * 
 * Note: This class assumes the existence of an ExtendNode utility that provides
 * methods like isDefinition and getDefinition.
 * 
 * Author: [Your Name]
 */
public class DefaultUnknownNodeFilter implements UnknownNodeFilter {
    public static final DefaultUnknownNodeFilter INSTANCE = new DefaultUnknownNodeFilter();

    /**
     * Processes a node within a parent ObjectNode. If the node's name matches a definition
     * and its type is correct, it is retained. Otherwise, it is removed from the parent.
     *
     * @param name   the name of the node to process
     * @param parent the parent ObjectNode containing the node
     */
    @Override
    public void process(String name, ObjectNode parent) {
        if (parent != null && name != null) {
            if (ExtendNode.isDefinition(name)) {
                if (ExtendNode.getDefinition(name).matchesType(parent.get(name))) {
                    return;
                }
            }
            parent.remove(name);
        }
    }

}
