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
package cn.taskflow.jcv.datatype;

import cn.taskflow.jcv.core.Primitive;
import cn.taskflow.jcv.exception.ValidationException;
import cn.taskflow.jcv.utils.JsvUtils;

/**
 * The NumberParser class is responsible for parsing a string representation of a number
 * into either a Double or Long type, and provides validation against specified constraints.
 */
public class NumberParser {

    // Indicates if the parsed number is of type Double
    public boolean isDouble;
    // Indicates if the parsed number is of type Long
    public boolean isLong;
    // Holds the parsed number value
    public Number  value;

    /**
     * Checks if the parsed number value is null.
     * 
     * @return true if the value is null, false otherwise.
     */
    public boolean isNull() {
        return this.value == null;
    }

    /**
     * Parses the given string into a NumberParser object. Determines if the number
     * is a Double or Long based on the presence of a decimal point.
     * 
     * @param value   the string representation of the number to parse.
     * @param require a boolean flag indicating if the value is required.
     * @return a NumberParser object containing the parsed number.
     */
    public static NumberParser parse(String value, boolean require) {
        NumberParser numberParser = new NumberParser();
        if (value != null) {
            if (value.indexOf(".") != -1) {
                numberParser.value = Double.parseDouble(value);
                numberParser.isDouble = true;
            } else {
                numberParser.value = Long.parseLong(value);
                numberParser.isLong = true;
            }
        }
        return numberParser;
    }

    /**
     * Validates the parsed number against the constraints defined in the given
     * Primitive object. Throws a ValidationException if the value does not meet
     * the required constraints.
     * 
     * @param primitive the Primitive object containing validation constraints.
     */
    public void check(Primitive primitive) {
        if (primitive.isRequired()) {
            if (value == null) {
                throw JsvUtils.throwMissingParamException(primitive.getName());
            }
        } else {
            if (value == null) {
                return;
            }
        }
        if (isDouble) {
            if (primitive.getMin() != null) {
                if (primitive.getMin().doubleValue() > value.doubleValue()) {
                    throw new ValidationException(primitive.getTipMsg(), primitive.getPath());
                }
            }
            if (primitive.getMax() != null) {
                if (primitive.getMax().doubleValue() < value.doubleValue()) {
                    throw new ValidationException(primitive.getTipMsg(), primitive.getPath());
                }
            }
        } else {
            if (primitive.getMin() != null) {
                if (primitive.getMin().longValue() > value.longValue()) {
                    throw new ValidationException(primitive.getTipMsg(), primitive.getPath());
                }
            }
            if (primitive.getMax() != null) {
                if (primitive.getMax().longValue() < value.longValue()) {
                    throw new ValidationException(primitive.getTipMsg(), primitive.getPath());
                }
            }
        }
    }

}
