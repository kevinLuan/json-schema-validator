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
Data validation middleware based on JSON DSL implementation. It provides developers with a powerful set of tools for defining and validating data structures.

### Features
* Flexible DSL definition: Allows users to describe complex JSON structures with simple DSL syntax. 
* Automatic code generation: Automatically generate Java classes from JSON schema, reducing manual coding effort. 
* Data Validation: Rich built-in validation rules ensure data is in the expected format. 
* Data extraction: Supports the extraction of specified fields from JSON documents, simplifying the data processing flow.

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
            JsonNumber.required("age"));
//The validity of the data is verified according to the schema definition

//data validation
Validator.fromSchema(jsonSchema).validate(json)
        
//Data Validation & Data Extraction
Validator.fromSchema(jsonSchema).validate(json).extract(json);

```

### custom validation extension

```java
JsonValidator customValidationRule = new JsonValidator() {
    @Override
    public boolean validate(JsonSchema schema, JsonNode node) throws ValidationException {
        /*Custom validation logic*/
        return true;
    }
};

//Add the validation policy withValidator(...) to any node in the Schema Optional
JsonSchema jsonSchema = JsonObject.required(
    JsonObject.required("item",
        JsonNumber.required("id").withValidator(customValidationRule),
        JsonArray.required("orderIds").withValidator(customValidationRule),
        JsonString.required("title").withValidator(customValidationRule)
    ).withValidator(customValidationRule),
    JsonString.required("name").withValidator(customValidationRule),
    JsonNumber.required("age").withValidator(customValidationRule)
    ).withValidator(customValidationRule);
        
//The data validation remains the same
Validator.fromSchema(jsonSchema).validate(...);
```

## License

json-schema-validator software is licensed under the Apache License Version 2.0. See the [LICENSE](https://www.apache.org/licenses/LICENSE-2.0) file for details.
