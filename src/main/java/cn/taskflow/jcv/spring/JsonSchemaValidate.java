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
package cn.taskflow.jcv.spring;

import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * Annotation for specifying JSON schema validation on method parameters or methods.
 * This annotation is used to indicate that the annotated element should be validated
 * against a specified JSON schema. The schema is identified by its name, which is
 * provided as the value of this annotation.
 * 
 * This annotation can be applied to method parameters and methods, and it is retained
 * at runtime to allow for reflection-based processing.
 * 
 * The annotation is also indexed for better performance in Spring applications.
 * 
 * Usage example:
 * 
 * <pre>
 * {@code
 * @JsonSchemaValidate("userSchema")
 * public void createUser(@JsonSchemaValidate("userSchema") User user) {
 *     // method implementation
 * }
 * }
 * </pre>
 * 
 * In the example above, both the method and the parameter are validated against the
 * "userSchema" JSON schema.
 * 
 * @see JsonSchemaFactory
 * @see JsonSchemaRequestBodyValidator
 * 
 * @since 2024-09-28
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface JsonSchemaValidate {
    /**
     * The name of the JSON schema to validate against.
     * 
     * @return the schema name
     */
    String value();
}
