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
 * @author SHOUSHEN.LUAN
 * @since 2024-02-04
 */
public interface VerifyHandler {
    String getTipError(String path);

    default ValidationException throwError(String path) {
        throw new ValidationException(getTipError(path), path);
    }

    String getTipMissing(String path);

    default ValidationException throwMissing(String path) {
        throw new ValidationException(getTipMissing(path), path);
    }
}
