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
package cn.taskflow.jcv.core;

import cn.taskflow.jcv.exception.ValidationException;
import cn.taskflow.jcv.utils.JsvUtils;

/**
 * Represents a primitive parameter, which is the smallest unit of a parameter.
 * This class extends the JsonBasicSchema and provides additional functionality
 * for handling primitive data types with optional range validation.
 *
 * @author KEVIN LUAN
 */
public class Primitive extends JsonBasicSchema {

    /**
     * Constructs a new Primitive instance with the specified attributes.
     *
     * @param name        the name of the primitive
     * @param require     whether the primitive is required
     * @param dataType    the data type of the primitive
     * @param description a description of the primitive
     * @param min         the minimum value for validation
     * @param max         the maximum value for validation
     * @throws IllegalArgumentException if the dataType is not a primitive type
     */
    public Primitive(String name, boolean require, DataType dataType, String description, Number min, Number max) {
        super(name, require, dataType, description);
        if (!DataType.isPrimitive(dataType)) {
            throw new IllegalArgumentException("Unsupported dataType: " + dataType);
        }
        this.min = min;
        this.max = max;
    }

    @Override
    public Primitive asPrimitive() {
        return this;
    }

    @Override
    public boolean isPrimitive() {
        return true;
    }

    /**
     * Gets the minimum value for validation.
     *
     * @return the minimum value
     */
    public Number getMin() {
        return min;
    }

    /**
     * Gets the maximum value for validation.
     *
     * @return the maximum value
     */
    public Number getMax() {
        return max;
    }

    /**
     * Sets the minimum value for validation and performs a check.
     *
     * @param min the minimum value to set
     * @return the current Primitive instance
     */
    public Primitive setMin(Number min) {
        this.min = min;
        this.check();
        return this;
    }

    /**
     * Validates the min and max values to ensure they are in a valid range.
     * Throws a ValidationException if the range is invalid.
     */
    private void check() {
        if (min != null && max != null) {
            if (String.valueOf(min).indexOf(".") != -1 || String.valueOf(max).indexOf(".") != -1) {
                if (min.doubleValue() > max.doubleValue()) {
                    throw new ValidationException("Validation parameters error. `" + max + "` must gt `" + min + "`",
                        getPath());
                }
            } else {
                if (min.longValue() > max.longValue()) {
                    throw new ValidationException("Validation parameters error. `" + max + "` must gt `" + min + "`",
                        getPath());
                }
            }
        }
    }

    /**
     * Sets both the minimum and maximum values for validation.
     *
     * @param min the minimum value to set
     * @param max the maximum value to set
     * @return the current Primitive instance
     */
    public Primitive between(Number min, Number max) {
        this.setMin(min);
        this.setMax(max);
        return this;
    }

    /**
     * Sets the maximum value for validation and performs a check.
     *
     * @param max the maximum value to set
     * @return the current Primitive instance
     */
    public Primitive setMax(Number max) {
        this.max = max;
        this.check();
        return this;
    }

    /**
     * Gets a tip message for the current path.
     *
     * @return the tip message
     */
    public String getTipMsg() {
        return this.getTipMsg(getPath());
    }

    /**
     * Gets a tip message for a specified path based on the data type and range.
     *
     * @param path the path to get the tip message for
     * @return the formatted tip message
     */
    public String getTipMsg(String path) {
        if (getDataType().isNumber()) {
            if (this.min != null && this.max != null) {
                return JsvUtils.formatBetween(path, min, max);
            } else if (this.min != null) {
                return JsvUtils.formatBetweenGtOrEq(path, min);
            } else if (this.max != null) {
                return JsvUtils.formatBetweenLtOrEq(path, max);
            } else {
                return JsvUtils.mustBeNumber(path);
            }
        } else if (getDataType().isString()) {
            if (this.min != null && this.max != null) {
                return JsvUtils.formatBetweenLength(path, min, max);
            } else if (this.min != null) {
                return JsvUtils.formatBetweenLengthGtOrEq(path, min);
            } else if (this.max != null) {
                return JsvUtils.formatBetweenLengthLtOrEq(path, max);
            }
        }
        return JsvUtils.formatParamError(path);
    }

    /**
     * Checks if there is a range validation present.
     *
     * @return true if either min or max is not null, false otherwise
     */
    public boolean existBetweenCheck() {
        return max != null || min != null;
    }

    /**
     * Gets the example value for this primitive.
     *
     * @return the example value as a string
     */
    public String getExampleValue() {
        return exampleValue;
    }

    /**
     * Sets an example value for this primitive, which can be used for mock data.
     *
     * @param exampleValue the example value to set
     * @return the current Primitive instance
     */
    public Primitive setExampleValue(Object exampleValue) {
        super.exampleValue = String.valueOf(exampleValue);
        return this;
    }
}
