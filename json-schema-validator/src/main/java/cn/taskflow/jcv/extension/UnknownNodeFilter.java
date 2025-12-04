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
 * This interface defines a contract for processing unknown JSON nodes.
 * Implementations of this interface should provide logic to handle nodes
 * that are not recognized or expected in a given JSON structure.
 */
public interface UnknownNodeFilter {
    /**
     * Processes an unknown node within a JSON structure.
     *
     * @param name   The name of the unknown node. This is the key associated with the node in the JSON object.
     * @param parent The parent ObjectNode that contains the unknown node. This allows access to the entire JSON structure
     *               for context or modification.
     */
    void process(String name, ObjectNode parent);
}
