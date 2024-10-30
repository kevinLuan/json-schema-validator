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

import cn.taskflow.jcv.exception.ValidationException;

/**
 * Interface for handling validation error messages and exceptions.
 * Provides methods to generate error messages and throw validation exceptions
 * for both validation errors and missing required fields.
 * 接口用于处理验证错误消息和异常。
 * 提供方法以生成错误消息并抛出验证异常，适用于验证错误和缺少必需字段的情况。
 *
 * @author SHOUSHEN.LUAN
 * @since 2024-02-04
 */
public interface VerifyHandler {
    /**
     * Gets the error message for a validation failure at the specified path
     * 获取指定路径的验证失败错误消息
     * @param path The JSON path where validation failed
     *             验证失败的JSON路径
     * @return Error message describing the validation failure
     *         描述验证失败的错误消息
     */
    String getTipError(String path);

    /**
     * Throws a ValidationException with the error message for the specified path
     * 抛出包含指定路径错误消息的ValidationException
     * @param path The JSON path where validation failed
     *             验证失败的JSON路径
     * @return Never returns, always throws ValidationException
     *         永不返回，总是抛出ValidationException
     * @throws ValidationException with error message and path
     *         包含错误消息和路径的ValidationException
     */
    default ValidationException throwError(String path) {
        throw new ValidationException(getTipError(path), path);
    }

    /**
     * Gets the error message for a missing required field at the specified path
     * 获取指定路径缺少必需字段的错误消息
     * @param path The JSON path of the missing required field
     *             缺少必需字段的JSON路径
     * @return Error message describing the missing field
     *         描述缺少字段的错误消息
     */
    String getTipMissing(String path);

    /**
     * Throws a ValidationException for a missing required field at the specified path
     * 为指定路径缺少的必需字段抛出ValidationException
     * @param path The JSON path of the missing required field
     *             缺少必需字段的JSON路径
     * @return Never returns, always throws ValidationException
     *         永不返回，总是抛出ValidationException
     * @throws ValidationException with missing field message and path
     *         包含缺少字段消息和路径的ValidationException
     */
    default ValidationException throwMissing(String path) {
        throw new ValidationException(getTipMissing(path), path);
    }
}
