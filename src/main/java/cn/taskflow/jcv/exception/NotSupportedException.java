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
 * This class represents an exception that is thrown when a requested operation
 * is not supported. It extends the RuntimeException, indicating that it is an
 * unchecked exception. This exception can be used to signal that a particular
 * method or operation is not implemented or not available in the current context.
 * 
 * Example usage:
 * <pre>
 *     throw new NotSupportedException("This operation is not supported.");
 * </pre>
 * 
 * @author SHOUSHEN.LUAN
 * @since 2024-02-03
 */
public class NotSupportedException extends RuntimeException {
    /**
     * Constructs a new NotSupportedException with the specified detail message.
     *
     * @param msg the detail message, which is saved for later retrieval by the
     *            {@link Throwable#getMessage()} method.
     */
    public NotSupportedException(String msg) {
        super(msg);
    }
}
