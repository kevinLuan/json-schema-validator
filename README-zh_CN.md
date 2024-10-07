json-schema-validator
============
<div align="left">
  <a href="javascript:void(0);"><img src="https://img.shields.io/badge/build-passing-brightgreen" /></a>
  <a href="javascript:void(0);" target="_blank"><img src="https://img.shields.io/badge/docs-latest-brightgreen" /></a>
  <a href="https://www.apache.org/licenses/LICENSE-2.0"><img src="https://img.shields.io/badge/License-Apache%202.0-blue.svg" alt="License"></a>
  <a href="https://central.sonatype.com/artifact/cn.taskflow.jsv/json-schema-validator?smo=true"><img src="https://img.shields.io/maven-metadata/v.svg?label=Maven%20Central&metadataUrl=https%3A%2F%2Frepo1.maven.org%2Fmaven2%2Fcn%2Ftaskflow%2Fjsv%2Fjson-schema-validator%2Fmaven-metadata.xml" alt="License"></a>
</div>

[English](./README.md) | 简体中文

## 简介
基于 JSON DSL (领域特定语言) 实现的数据验证中间件。它为开发者提供了强大的工具集，用于定义和验证 JSON 数据结构。此框架不仅支持任意 JSON 格式的自动代码生成，还能够基于 DSL 定义的 schema 来实现数据验证及数据提取。

### 特点
灵活的 DSL 定义：允许用户通过简单的 DSL 语法来描述复杂的 JSON 结构。
自动代码生成：从 JSON schema 自动生成 Java 类，减少手动编码工作量。
数据验证：内置丰富的验证规则，确保数据符合预期格式。
数据提取：支持从 JSON 文档中提取指定字段，简化数据处理流程。

### 快速开始

#### 添加依赖
### Maven dependency
```xml
<dependency>
    <groupId>cn.taskflow.jsv</groupId>
    <artifactId>json-schema-validator</artifactId>
    <version>0.1.1</version>
</dependency>
```


### 代码生成

#### JSON 数据
```json
{
  "item": {
    "id": 1000,
    "orderIds": [ 1, 2, 3, 4, 5 ],
    "title": "item name"
  },
  "name": "张三丰",
  "age": 60
} 
```
```java
String json =...;
//使用json生成JSON schema 代码
String javaCode = CodeGenerationUtils.generateSchemaCode(json,new GenerateOptional());
System.out.println(javaCode);
```
生成后的代码如下：
```java
JsonSchema jsonSchema = JsonObject.required(
        JsonObject.required("item",
            JsonNumber.required("id"),
                JsonArray.required("orderIds"),
                JsonString.required("title")
            ),
        JsonString.required("name"),
        JsonNumber.required("age"));
    
//数据验证
Validator.of(jsonSchema).validate(json)
//数据验证&数据提取
Validator.of(jsonSchema).validate(json).extract(json);
```
### 自定义验证扩展

```java
JsonValidator jsonValidator = new JsonValidator() {
    @Override
    public boolean validate(JsonSchema schema, JsonNode node) throws ValidationException {
        //自定义验证逻辑
        return true;
    }
};

//在 Schema 的任意节点上增加验证策略 withValidator(...) 可选
JsonSchema jsonSchema = JsonObject.required(
    JsonObject.required("item",
        JsonNumber.required("id").withValidator(jsonValidator),
        JsonArray.required("orderIds").withValidator(jsonValidator),
        JsonString.required("title").withValidator(jsonValidator)
    ).withValidator(jsonValidator),
        JsonString.required("name").withValidator(jsonValidator),
        JsonNumber.required("age").withValidator(jsonValidator)
    ).withValidator(jsonValidator);
    //数据验证保持不变
    Validator.of(jsonSchema).validate(...);

```

## 许可证

[Apache 2.0 许可证](https://www.apache.org/licenses/LICENSE-2.0)

版权所有 (c) 2024 json-schema-validator