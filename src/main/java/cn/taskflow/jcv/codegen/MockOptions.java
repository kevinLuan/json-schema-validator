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
package cn.taskflow.jcv.codegen;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-11-29
 */
public class MockOptions {
    private int arraySize = 1;
    private int mapSize   = 1;

    // Private constructor to enforce builder usage
    private MockOptions() {
    }

    // Getters
    public int getArraySize() {
        return arraySize;
    }

    public int getMapSize() {
        return mapSize;
    }

    // Builder class
    public static class Builder {
        private final MockOptions options;

        public Builder() {
            options = new MockOptions();
        }

        public Builder arraySize(int size) {
            options.arraySize = size;
            return this;
        }

        public Builder mapSize(int size) {
            options.mapSize = size;
            return this;
        }

        public MockOptions build() {
            return options;
        }
    }

    // Static factory method for builder
    public static Builder builder() {
        return new Builder();
    }

    // Static factory method for default options
    public static MockOptions defaultOptions() {
        return builder().build();
    }

    // Override toString for debugging
    @Override
    public String toString() {
        return "MockOptions{" + "arrayValueSize=" + arraySize + ", mapValueSize=" + mapSize + '}';
    }
}
