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

public class GenerateOptions {
    /**
     * 标志指示是否生成示例值
     */
    private boolean generateExample = false;
    /**
     * 标志指示是否生成描述
     */
    private boolean generateDesc    = false;
    /**
     * 标志指示字段是否为必需
     */
    private boolean require         = true;

    private GenerateOptions() {
    }

    private GenerateOptions(GenerateOptions options) {
        this.generateExample = options.generateExample;
        this.generateDesc = options.generateDesc;
        this.require = options.require;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static GenerateOptions defaultOptions() {
        return GenerateOptions.builder().build();
    }

    /**
     * Checks if example values should be generated.
     * <p>
     * 检查是否应生成示例值。
     *
     * @return true if example values should be generated, false otherwise
     * @return 如果应生成示例值，则返回true，否则返回false
     */
    public boolean isGenerateExample() {
        return generateExample;
    }

    /**
     * Checks if descriptions should be generated.
     * <p>
     * 检查是否应生成描述。
     *
     * @return true if descriptions should be generated, false otherwise
     * @return 如果应生成描述，则返回true，否则返回false
     */
    public boolean isGenerateDesc() {
        return generateDesc;
    }

    /**
     * Checks if fields are required.
     * <p>
     * 检查字段是否为必需。
     *
     * @return true if fields are required, false otherwise
     * @return 如果字段为必需，则返回true，否则返回false
     */
    public boolean isRequire() {
        return require;
    }

    public static class Builder {
        private final GenerateOptions options;

        private Builder() {
            options = new GenerateOptions();
        }

        public Builder generateExample(boolean generateExample) {
            options.generateExample = generateExample;
            return this;
        }

        public Builder generateDesc(boolean generateDesc) {
            options.generateDesc = generateDesc;
            return this;
        }

        public Builder require(boolean require) {
            options.require = require;
            return this;
        }

        public GenerateOptions build() {
            return new GenerateOptions(options);
        }
    }
}
