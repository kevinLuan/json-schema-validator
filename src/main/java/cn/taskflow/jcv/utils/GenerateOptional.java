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

/**
 * The GenerateOptional class is used to specify options for generating code from JSON schemas.
 * It allows configuration of whether to generate example values, descriptions, and whether fields are required.
 * This class provides constructors to initialize these options and getter methods to access their values.
 * 
 * Usage:
 * - Create an instance of GenerateOptional with specific options for code generation.
 * - Use the getter methods to check the configured options.
 * 
 * Example:
 * <pre>
 *     GenerateOptional options = new GenerateOptional(true, false, true);
 *     boolean generateExample = options.isGenerateExample();
 * </pre>
 * 
 * @see CodeGenerationUtils
 * @since 2024-09-26
 * 
 * Author: SHOUSHEN.LUAN
 */
public class GenerateOptional {
    /** Flag to indicate whether to generate example values */
    private boolean generateExample;
    /** Flag to indicate whether to generate descriptions */
    private boolean generateDesc;
    /** Flag to indicate whether fields are required */
    private boolean require;

    /**
     * Constructs a GenerateOptional instance with specified options.
     *
     * @param generateExample whether to generate example values
     * @param generateDesc whether to generate descriptions
     * @param require whether fields are required
     */
    public GenerateOptional(boolean generateExample, boolean generateDesc, boolean require) {
        this.generateExample = generateExample;
        this.generateDesc = generateDesc;
        this.require = require;
    }

    /**
     * Constructs a GenerateOptional instance with default options.
     * By default, fields are set to be required.
     */
    public GenerateOptional() {
        require = true;
    }

    /**
     * Checks if example values should be generated.
     *
     * @return true if example values should be generated, false otherwise
     */
    public boolean isGenerateExample() {
        return generateExample;
    }

    /**
     * Checks if descriptions should be generated.
     *
     * @return true if descriptions should be generated, false otherwise
     */
    public boolean isGenerateDesc() {
        return generateDesc;
    }

    /**
     * Checks if fields are required.
     *
     * @return true if fields are required, false otherwise
     */
    public boolean isRequire() {
        return require;
    }
}
