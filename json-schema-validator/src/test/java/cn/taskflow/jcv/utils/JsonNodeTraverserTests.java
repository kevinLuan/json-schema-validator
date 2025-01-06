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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author KEVIN.LUAN
 * @since 2025-01-05
 */
public class JsonNodeTraverserTests {
    public static void main(String[] args) throws JsonProcessingException {
        String json = " {\n" +
                "            \"id\": 1001,\n" +
                "            \"name\": \"John Doe\",\n" +
                "            \"active\": true,\n" +
                "            \"score\": 95.5,\n" +
                "            \"contact\": {\n" +
                "                \"email\": \"john@example.com\",\n" +
                "                \"phone\": {\n" +
                "                    \"home\": \"123-456-7890\",\n" +
                "                    \"work\": \"098-765-4321\"\n" +
                "                },\n" +
                "                \"address\": {\n" +
                "                    \"street\": \"123 Main St\",\n" +
                "                    \"city\": \"Boston\",\n" +
                "                    \"country\": \"USA\"\n" +
                "                }\n" +
                "            },\n" +
                "            \"skills\": [\"Java\", \"Python\", \"JavaScript\"],\n" +
                "            \"projects\": [\n" +
                "                {\n" +
                "                    \"name\": \"Project A\",\n" +
                "                    \"status\": \"completed\",\n" +
                "                    \"members\": [\"Alice\", \"Bob\"]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"Project B\",\n" +
                "                    \"status\": \"in-progress\",\n" +
                "                    \"members\": [\"John\", \"Mary\"]\n" +
                "                }\n" +
                "            ],\n" +
                "            \"certificates\": null\n" +
                "        }";
        JsonNodeTraverser.traverse(json, (path, node) -> {
            if (node.isValueNode()) {
                System.out.println(path + " --> " + node.asText());
            }
        });
    }
}
