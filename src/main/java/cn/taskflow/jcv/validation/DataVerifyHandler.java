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
package cn.taskflow.jcv.validation;

import cn.taskflow.jcv.utils.JsvUtils;

/**
 * Default implementation of VerifyHandler interface that provides validation error handling.
 * This handler formats error messages for validation failures and missing required fields
 * using utility methods from JsvUtils.
 *
 * @author SHOUSHEN.LUAN
 * @since 2024-02-04
 */
public class DataVerifyHandler implements VerifyHandler {
    /**
     * Factory method to get a singleton instance of the DataVerifyHandler
     * @return A new instance of VerifyHandler
     */
    public static VerifyHandler getInstance() {
        return new DataVerifyHandler();
    }

    /**
     * Formats an error message for validation failures at the specified JSON path
     * @param path The JSON path where validation failed
     * @return Formatted error message for validation failure
     */
    @Override
    public String getTipError(String path) {
        return JsvUtils.formatParamError(path);
    }

    /**
     * Formats an error message for missing required fields at the specified JSON path
     * @param path The JSON path of the missing required field
     * @return Formatted error message for missing field
     */
    @Override
    public String getTipMissing(String path) {
        return JsvUtils.formatParamMissing(path);
    }
}
