json-schema-validator
============
<div align="left">
  <a href="javascript:void(0);"><img src="https://img.shields.io/badge/build-passing-brightgreen" /></a>
  <a href="javascript:void(0);" target="_blank"><img src="https://img.shields.io/badge/docs-latest-brightgreen" /></a>
  <a href="https://www.apache.org/licenses/LICENSE-2.0"><img src="https://img.shields.io/badge/License-Apache%202.0-blue.svg" alt="License"></a>
  <a href="https://central.sonatype.com/artifact/cn.taskflow.jsv/json-schema-validator?smo=true"><img src="https://img.shields.io/maven-metadata/v.svg?label=Maven%20Central&metadataUrl=https%3A%2F%2Frepo1.maven.org%2Fmaven2%2Fcn%2Ftaskflow%2Fjsv%2Fjson-schema-validator%2Fmaven-metadata.xml" alt="License"></a>
</div>

English | [简体中文](./README-zh_CN.md)

## Overview
A data validation middleware based on JSON DSL (Domain-Specific Language). It provides developers with a powerful toolkit for defining and validating JSON data structures. This framework not only supports automatic code generation for any JSON format but also enables data validation and extraction based on schemas defined using the DSL.

### Features
1. Flexible DSL Definition: Allows users to describe complex JSON structures using a simple DSL syntax.
   Automatic Code Generation: Automatically generates Java classes from JSON schemas, reducing manual coding efforts.
   Data Validation: Incorporates a rich set of built-in validation rules to ensure data conforms to expected formats.
4. Data Extraction: Supports extracting specified fields from JSON documents, simplifying data processing workflows.
   This translation maintains the structure of the original list while accurately conveying the meaning of each feature in English. The technical terms and concepts are preserved to ensure clarity for an English-speaking technical audience.

### Installation
To integrate TaskFlow into your Java project.

The Maven project adds the following dependency to your 'pom.xml' file：
```xml
<dependency>
    <groupId>cn.taskflow.jsv</groupId>
    <artifactId>json-schema-validator</artifactId>
    <version>latest</version>
</dependency>
```
Add the following dependencies to the gradle project:
```text
implementation 'cn.taskflow.jsv:json-schema-validator:latest'
```

### Code generation sample program

#### JSON data
```json
{
  "item": {
    "id": 1000,
    "orderIds": [ 1, 2, 3, 4, 5 ],
    "title": "item name"
  },
  "name": "shanfeng Zhang",
  "age": 60
} 
```
```java
String json =...;
//JSON schema code is generated using JSON
String javaCode = CodeGenerationUtils.generateSchemaCode(json,new GenerateOptional());
System.out.println(javaCode);
```
The resulting code looks like this:：
```java
    JsonSchema jsonSchema = JsonObject.required(
                JsonObject.required("item",
                        JsonNumber.required("id"),
                        JsonArray.required("orderIds"),
                        JsonString.required("title")
                ),
                JsonString.required("name"),
                JsonNumber.required("age")
        );
    //The validity of the data is verified according to the schema definition
    
    //data validation
    Validator.of(jsonSchema).validate(json)
        
    //Data Validation & Data Extraction
    Validator.of(jsonSchema).validate(json).extract(json);
```

### custom validation extension

```java
    JsonValidator jsonValidator = new JsonValidator() {
        @Override
        public boolean validate(JsonSchema schema, JsonNode node) throws ValidationException {
            /*Custom validation logic*/
            return true;
        }
    };

    //Add the validation policy withValidator(...) to any node in the Schema Optional
    JsonSchema jsonSchema = JsonObject.required(
            JsonObject.required("item",
                JsonNumber.required("id").withValidator(jsonValidator),
                JsonArray.required("orderIds").withValidator(jsonValidator),
                JsonString.required("title").withValidator(jsonValidator)
        ).withValidator(jsonValidator),
        JsonString.required("name").withValidator(jsonValidator),
        JsonNumber.required("age").withValidator(jsonValidator)
        ).withValidator(jsonValidator);
        
    //The data validation remains the same
    Validator.of(jsonSchema).validate(...);

```

## License

json-schema-validator software is licensed under the Apache License Version 2.0. See the [LICENSE](https://www.apache.org/licenses/LICENSE-2.0) file for details.
