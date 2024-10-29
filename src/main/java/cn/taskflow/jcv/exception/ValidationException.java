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
package cn.taskflow.jcv.exception;

/**
 * ValidationException is a custom exception that extends IllegalArgumentException.
 * It is used to indicate that a validation error has occurred, providing a message
 * and the path where the error was found.
 * 
 * This exception is particularly useful in scenarios where input validation is
 * critical, and the path information helps in pinpointing the exact location
 * of the validation failure within a data structure or configuration.
 * 
 * @author SHOUSHEN.LUAN
 * @since 2024-09-25
 */
public class ValidationException extends IllegalArgumentException {
    private String path; // The path where the validation error occurred

    /**
     * Constructs a new ValidationException with the specified detail message and path.
     *
     * @param msg  the detail message, saved for later retrieval by the getMessage() method.
     * @param path the path where the validation error occurred, saved for later retrieval by the getPath() method.
     */
    public ValidationException(String msg, String path) {
        super(msg);
        this.path = path;
    }

    /**
     * Returns the path where the validation error occurred.
     *
     * @return the path as a String.
     */
    public String getPath() {
        return this.path;
    }
}
