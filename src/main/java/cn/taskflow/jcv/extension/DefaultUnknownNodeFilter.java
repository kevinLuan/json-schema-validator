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

public class DefaultUnknownNodeFilter implements UnknownNodeFilter {
    public static final DefaultUnknownNodeFilter INSTANCE = new DefaultUnknownNodeFilter();

    /**
     * 所有对象节点均支持extendProps及remark属性，只要符是合要求格式的均跳过处理，否则删除该节点
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
