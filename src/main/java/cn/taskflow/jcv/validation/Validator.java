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

import cn.taskflow.jcv.core.*;
import cn.taskflow.jcv.encode.NodeFactory;
import cn.taskflow.jcv.exception.ValidationException;
import cn.taskflow.jcv.extension.JsonSchemaTypeAdjuster;
import cn.taskflow.jcv.extension.JsonSchemaParentRefresher;
import cn.taskflow.jcv.extension.UnknownNodeFilter;
import cn.taskflow.jcv.utils.JsvUtils;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import cn.taskflow.jcv.datatype.NumberParser;

import java.util.*;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Main validator class for JSON schema validation
 */
public class Validator {
    private AbstractDataValidator dataValidator;

    /**
     * Sets a filter for handling unknown nodes during validation
     * @param filter The filter to handle unknown nodes
     * @return This validator instance
     */
    public Validator setUnknownNodeFilter(UnknownNodeFilter filter) {
        this.dataValidator.setUnknownNodeFilter(filter);
        return this;
    }

    /**
     * Creates a validator from JSON schemas using default verify handler
     * @param jsonSchemas The JSON schemas to validate against
     * @return A new validator instance
     */
    public static Validator fromSchema(JsonSchema... jsonSchemas) {
        return fromSchema(DataVerifyHandler.getInstance(), jsonSchemas);
    }

    /**
     * Creates a validator from JSON schemas with custom verify handler
     * @param verifyHandler Custom verify handler
     * @param jsonSchemas The JSON schemas to validate against
     * @return A new validator instance
     */
    public static Validator fromSchema(VerifyHandler verifyHandler, JsonSchema... jsonSchemas) {
        Validator validator = new Validator();
        validator.dataValidator = AbstractDataValidator.make(verifyHandler, jsonSchemas);
        return validator;
    }

    /**
     * Validates data supplied by a function
     * @param dataSupplier Function that supplies data as strings
     * @return This validator instance
     */
    public Validator validate(Function<String, String> dataSupplier) {
        dataValidator.validate(dataSupplier);
        return this;
    }

    /**
     * Extracts data supplied by a function into a map
     * @param dataSupplier Function that supplies data as strings
     * @return Map of extracted data
     */
    public Map<String, Object> extract(Function<String, String> dataSupplier) {
        return dataValidator.extract(dataSupplier);
    }

    /**
     * Validates a JsonNode against the schema
     * @param jsonNode The node to validate
     * @return This validator instance
     */
    public Validator validate(JsonNode jsonNode) {
        dataValidator.validate(jsonNode);
        return this;
    }

    /**
     * Validates a JSON string against the schema
     * @param json The JSON string to validate
     * @return This validator instance
     */
    public Validator validate(String json) {
        dataValidator.validate(NodeFactory.parser(json));
        return this;
    }

    /**
     * Validates an object by converting it to JSON first
     * @param obj The object to validate
     * @return This validator instance
     */
    public Validator validate(Object obj) {
        dataValidator.validate(NodeFactory.convert(obj));
        return this;
    }

    /**
     * Extracts data from a JsonNode into a map
     * @param json The JsonNode to extract from
     * @return Map of extracted data
     */
    public Map<String, Object> extract(JsonNode json) {
        return dataValidator.extract(json);
    }

    /**
     * Extracts data from an object by converting it to JSON first
     * @param obj The object to extract from
     * @return Map of extracted data
     */
    public Map<String, Object> extract(Object obj) {
        JsonNode json = NodeFactory.convert(obj);
        return dataValidator.extract(json);
    }

    /**
     * Extracts data from a JSON string into a map
     * @param json The JSON string to extract from
     * @return Map of extracted data
     */
    public Map<String, Object> extract(String json) {
        JsonNode jsonNode = NodeFactory.parser(json);
        return dataValidator.extract(jsonNode);
    }

    /**
     * Abstract class for data validation implementation
     */
    public static class AbstractDataValidator {
        private UnknownNodeFilter filter;
        private VerifyHandler     verifyHandler;

        /**
         * Sets the filter for handling unknown nodes
         * @param filter The filter to use
         */
        public void setUnknownNodeFilter(UnknownNodeFilter filter) {
            this.filter = filter;
        }

        public List<JsonSchema>     jsonSchemas;
        private DataStructValidator dataStructValidator = DataStructValidator.getInstance(this);

        /**
         * Constructor for AbstractDataValidator
         * @param jsonSchemas List of JSON schemas to validate against
         * @param verifyHandler Handler for verification
         */
        public AbstractDataValidator(List<JsonSchema> jsonSchemas, VerifyHandler verifyHandler) {
            this.jsonSchemas = jsonSchemas;
            JsonSchemaTypeAdjuster.adjust(jsonSchemas);
            JsonSchemaParentRefresher.refreshParentReference(jsonSchemas);
            this.verifyHandler = verifyHandler;
        }

        /**
         * Validates a JsonNode against the first schema
         * @param jsonNode Node to validate
         * @return This validator instance
         */
        public AbstractDataValidator validate(JsonNode jsonNode) {
            dataStructValidator.validate(jsonSchemas.get(0), jsonNode);
            return this;
        }

        /**
         * Extracts data from a JsonNode according to schema
         * @param jsonNode Node to extract from
         * @return Map of extracted data
         */
        public Map<String, Object> extract(JsonNode jsonNode) {
            JsonSchema jsonSchema = jsonSchemas.get(0);
            object(jsonNode, jsonSchema);
            Iterator<String> iterator = jsonNode.fieldNames();
            Map<String, Object> data = new HashMap<>(jsonSchema.asObject().getChildren().length);
            while (iterator.hasNext()) {
                String key = iterator.next();
                data.put(key, jsonNode.get(key));
            }
            return data;
        }

        /**
         * Validates data from a supplier function
         * @param dataSupplier Function supplying data
         * @return This validator instance
         */
        public AbstractDataValidator validate(Function<String, String> dataSupplier) {
            dataStructValidator.validate(dataSupplier, jsonSchemas.toArray(new JsonSchema[0]));
            return this;
        }

        /**
         * Extracts data from a supplier function according to schema
         * @param dataSupplier Function supplying data
         * @return Map of extracted data
         */
        public Map<String, Object> extract(Function<String, String> dataSupplier) {
            Map<String, Object> data = new HashMap<>(jsonSchemas.size());
            for (JsonSchema jsonSchema : jsonSchemas) {
                String value = dataSupplier.apply(jsonSchema.getName());
                if (value == null) {
                    continue;
                }
                if (jsonSchema.isPrimitive()) {
                    data.put(jsonSchema.getName(), value);
                } else {
                    JsonNode node = NodeFactory.parser(value);
                    if (jsonSchema.isArray()) {
                        if (node.isArray()) {
                            array(node, jsonSchema.asArray());
                        } else {
                            verifyHandler.throwError(jsonSchema.getPath());
                        }
                    } else if (jsonSchema.isObject()) {
                        object(node, jsonSchema.asObject());
                    } else {
                        throw new ValidationException("Unsupported operation: " + jsonSchema, jsonSchema.getPath());
                    }
                    data.put(jsonSchema.getName(), node);
                }
            }
            return data;
        }

        /**
         * Creates a new AbstractDataValidator instance
         * @param verifyHandler Handler for verification
         * @param jsonSchemas Schemas to validate against
         * @return New AbstractDataValidator instance
         */
        public static AbstractDataValidator make(VerifyHandler verifyHandler, JsonSchema... jsonSchemas) {
            return new AbstractDataValidator(Arrays.asList(jsonSchemas), verifyHandler);
        }

        /**
         * Validates and processes an object node
         * @param node Node to validate
         * @param jsonSchema Schema to validate against
         */
        protected void object(JsonNode node, JsonSchema jsonSchema) {
            if (!node.isObject() || !jsonSchema.isObject()) {
                verifyHandler.throwError(jsonSchema.getPath());
            }
            ObjectNode objectNode = (ObjectNode) node;
            JsonObject paramObject = jsonSchema.asObject();
            if (!paramObject.existsChildren()) {
                return;// If no child nodes configured, preserve all child nodes under current node
            }
            JsonSchema[] children = paramObject.getChildren();
            delete(objectNode, children);
            for (int i = 0; i < children.length; i++) {
                JsonSchema childrenSchema = children[i];
                JsonNode jsonNode = objectNode.get(childrenSchema.getName());
                if (!isEmptyNode(jsonNode)) {
                    if (childrenSchema.isObject()) {
                        object(jsonNode, childrenSchema);
                    } else if (childrenSchema.isArray()) {
                        array(jsonNode, childrenSchema);
                    }
                }
            }
        }

        /**
         * Deletes nodes that don't exist in schema
         * @param objectNode Node to clean
         * @param children Schema children to check against
         */
        void delete(ObjectNode objectNode, JsonSchema[] children) {
            Iterator<String> iterator = objectNode.fieldNames();
            List<String> deletes = new LinkedList<>();
            while (iterator.hasNext()) {
                String name = iterator.next();
                if (!exists(children, name)) {
                    deletes.add(name);
                }
            }
            if (filter != null) {
                for (String key : deletes) {
                    filter.process(key, objectNode);
                }
            } else {
                for (String key : deletes) {
                    objectNode.remove(key);
                }
            }
        }

        /**
         * Checks if a field name exists in schema children
         * @param children Schema children to check
         * @param name Field name to look for
         * @return true if exists, false otherwise
         */
        boolean exists(JsonSchema[] children, String name) {
            for (JsonSchema p : children) {
                if (name.equals(p.getName())) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Validates and processes an array node
         * @param jsonNode Node to validate
         * @param jsonSchema Schema to validate against
         */
        void array(JsonNode jsonNode, JsonSchema jsonSchema) {
            if (!jsonNode.isArray() || !jsonSchema.isArray()) {
                verifyHandler.throwError(jsonSchema.getPath());
            }
            JsonArray array = (JsonArray) jsonSchema;
            ArrayNode arrayNode = (ArrayNode) jsonNode;
            if (!array.existsChildren()) {
                return;
            }
            Optional<JsonSchema> optional = array.getSchemaForFirstChildren();
            if (optional.isPresent()) {
                for (int i = 0; i < arrayNode.size(); i++) {
                    JsonNode node = arrayNode.get(i);
                    if (node.isObject()) {
                        object(node, optional.get());
                    }
                }
            }
        }

        /**
         * Checks if a node is empty or null
         * @param node Node to check
         * @return true if empty, false otherwise
         */
        public boolean isEmptyNode(JsonNode node) {
            if (node == null || node.isNull() || node.isMissingNode()) {
                return true;
            }
            return false;
        }
    }

    /**
     * Validator for data structure validation
     */
    public static class DataStructValidator {
        private AbstractDataValidator dataValidator;

        private DataStructValidator(AbstractDataValidator dataValidator) {
            this.dataValidator = dataValidator;
        }

        /**
         * Creates a new DataStructValidator instance
         * @param dataValidator The abstract validator to use
         * @return New DataStructValidator instance
         */
        public static DataStructValidator getInstance(AbstractDataValidator dataValidator) {
            return new DataStructValidator(dataValidator);
        }

        /**
         * Validates a JsonNode against a schema
         * @param jsonSchema Schema to validate against
         * @param jsonNode Node to validate
         */
        public void validate(JsonSchema jsonSchema, JsonNode jsonNode) {
            if (jsonSchema == null) {
                throw new IllegalArgumentException("param must be not null");
            }
            if (jsonSchema.isRequired()) {
                if (NodeFactory.isNull(jsonNode)) {
                    dataValidator.verifyHandler.throwMissing(jsonSchema.getPath());
                }
            } else {
                if (NodeFactory.isNull(jsonNode)) {
                    jsonSchema.verify(jsonNode);
                    return;
                }
            }
            if (jsonSchema.isPrimitive()) {
                checkSimple(jsonSchema, jsonNode);
            } else if (jsonSchema.isArray()) {
                checkArray(jsonSchema, jsonNode);
            } else if (jsonSchema.isObject()) {
                checkObject(jsonSchema, jsonNode);
            } else {
                throw new ValidationException("Unsupported type: " + jsonSchema, jsonSchema.getPath());
            }
        }

        /**
         * Validates data from a supplier against schemas
         * @param dataSupplier Function supplying data
         * @param jsonSchemas Schemas to validate against
         */
        public void validate(Function<String, String> dataSupplier, JsonSchema... jsonSchemas) {
            if (dataSupplier == null) {
                throw new IllegalArgumentException("request must be not null");
            }
            for (JsonSchema jsonSchema : jsonSchemas) {
                String name = jsonSchema.getName();
                String value = dataSupplier.apply(name);
                if (jsonSchema.isRequired()) {
                    if (value == null) {
                        dataValidator.verifyHandler.throwMissing(jsonSchema.getPath());
                    }
                } else {
                    if (value == null) {
                        continue;
                    }
                }
                if (jsonSchema.isPrimitive()) {
                    checkSimple(jsonSchema, JsonNodeFactory.instance.textNode(value));
                } else {
                    JsonNode jsonNode = null;
                    try {
                        jsonNode = NodeFactory.parser(value);
                    } catch (Exception e) {
                        dataValidator.verifyHandler.throwError(jsonSchema.getPath());
                    }
                    if (jsonSchema.isArray()) {
                        checkArray(jsonSchema, jsonNode);
                    } else if (jsonSchema.isObject()) {
                        checkObject(jsonSchema, jsonNode);
                    } else {
                        throw new ValidationException("Unsupported type: " + jsonSchema, jsonSchema.getPath());
                    }
                }
            }
        }

        /**
         * Validates an array node against schema
         * @param jsonSchema Schema to validate against
         * @param value Node to validate
         */
        void checkArray(JsonSchema jsonSchema, JsonNode value) {
            if (jsonSchema.isArray() && value.isArray()) {
                JsonArray array = jsonSchema.asArray();
                if (!array.existsChildren()) {
                    jsonSchema.verify(value);
                    return;
                }

                if (array.isRequired()) {
                    if (value.size() == 0) {
                        throw JsvUtils.throwParamException(jsonSchema.getPath());
                    }
                }
                jsonSchema.verify(value);
                Optional<JsonSchema> optional = array.getSchemaForFirstChildren();
                if (optional.isPresent()) {
                    JsonSchema children = optional.get();
                    for (int i = 0; i < value.size(); i++) {
                        JsonNode node = value.get(i);
                        if (children.isObjectValue()) {
                            checkObject(children, (ObjectNode) node);
                        } else if (children.isPrimitive()) {
                            checkSimple(children.asPrimitive(), node);
                        } else {
                            throw new ValidationException("Unsupported type: " + children, children.getPath());
                        }
                    }
                }
            } else {
                dataValidator.verifyHandler.throwError(jsonSchema.getPath());
            }
        }

        /**
         * Validates a simple (primitive) value against schema
         * @param jsonSchema Schema to validate against
         * @param node Node to validate
         */
        void checkSimple(JsonSchema jsonSchema, JsonNode node) {
            if (jsonSchema.isPrimitive()) {
                if (node.isObject() || node.isArray()) {
                    dataValidator.verifyHandler.throwError(jsonSchema.getPath());
                }
                String value = NodeFactory.toString(node);
                if (jsonSchema.getDataType().isNumber()) {
                    try {
                        NumberParser.parse(value, jsonSchema.isRequired()).check(jsonSchema.asPrimitive());
                    } catch (NumberFormatException e) {
                        if (jsonSchema.getParentNode() != null) {
                            if (jsonSchema.getParentNode().isArray()) {
                                if (jsonSchema.asPrimitive().existBetweenCheck()) {
                                    String msg = jsonSchema.asPrimitive().getTipMsg(jsonSchema.getPath() + "[]");
                                    throw new ValidationException(msg, jsonSchema.getPath());
                                } else {
                                    throw JsvUtils.throwParamException(jsonSchema.getPath());
                                }
                            }
                        }
                        if (jsonSchema.asPrimitive().existBetweenCheck()) {
                            String msg = jsonSchema.asPrimitive().getTipMsg(jsonSchema.getPath());
                            throw new ValidationException(msg, jsonSchema.getPath());
                        } else {
                            throw JsvUtils.throwParamException(jsonSchema.getPath());
                        }
                    }
                    jsonSchema.verify(node);
                } else if (jsonSchema.getDataType().isString()) {
                    DataType.String.check(jsonSchema.asPrimitive(), value);
                    jsonSchema.verify(node);
                } else if (jsonSchema.getDataType().isBoolean()) {
                    DataType.Boolean.check(jsonSchema.asPrimitive(), value);
                    jsonSchema.verify(node);
                } else {
                    throw new ValidationException("Unsupported type: " + jsonSchema.getDataType(), jsonSchema.getPath());
                }
            }
        }

        /**
         * Validates an object node against schema
         * @param jsonSchema Schema to validate against
         * @param jsonNode Node to validate
         */
        void checkObject(JsonSchema jsonSchema, JsonNode jsonNode) {
            if (!jsonSchema.isObject() || !jsonNode.isObject()) {
                dataValidator.verifyHandler.throwError(jsonSchema.getPath());
            }
            jsonSchema.verify(jsonNode);
            JsonObject obj = jsonSchema.asObject();
            ObjectNode objNode = (ObjectNode) jsonNode;
            for (JsonSchema p : obj.getChildren()) {
                JsonNode value = objNode.get(p.getName());
                if (p.isRequired()) {
                    if (NodeFactory.isNull(value)) {
                        dataValidator.verifyHandler.throwMissing(p.getPath());
                    }
                } else {
                    if (NodeFactory.isNull(value)) {
                        p.verify(value);
                        continue;
                    }
                }
                if (p.isObject()) {
                    checkObject(p, value);
                } else if (p.isArray()) {
                    checkArray(p, value);
                } else if (p.isPrimitive()) {
                    checkSimple(p, value);
                } else {
                    throw new ValidationException("Unsupported type: " + p, p.getPath());
                }
            }
        }
    }
}
