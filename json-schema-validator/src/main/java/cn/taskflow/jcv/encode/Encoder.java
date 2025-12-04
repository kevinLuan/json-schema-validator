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
package cn.taskflow.jcv.encode;

public interface Encoder {
    /**
     * Serialization API
     * 
     * This method is responsible for converting an object into a string representation.
     * It is typically used to prepare data for storage or transmission.
     *
     * @param t the object to be serialized
     * @return a string representation of the object
     */
    String encode(Object t);

    /**
     * Deserialization API
     * 
     * This method is responsible for converting a string back into an object of the specified type.
     * It is used to reconstruct the original object from its string representation.
     *
     * @param data the string representation of the object
     * @param type the class type of the object to be deserialized
     * @return an instance of the specified type
     */
    <T> T decode(String data, Class<T> type);
}
