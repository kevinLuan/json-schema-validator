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
基于 JSON DSL 实现的数据验证中间件。它为开发者提供了强大的工具集，用于定义和验证数据结构。

### 特点
* 灵活的 DSL 定义：允许用户通过简单的 DSL 语法来描述复杂的 JSON 结构。
* 自动代码生成：从 JSON schema 自动生成 Java 类，减少手动编码工作量。
* 数据验证：内置丰富的验证规则，确保数据符合预期格式。
* 数据提取：支持从 JSON 文档中提取指定字段，简化数据处理流程。

### 快速开始

#### 添加依赖
### Maven dependency
```xml
<dependency>
    <groupId>cn.taskflow.jsv</groupId>
    <artifactId>json-schema-validator</artifactId>
    <version>0.1.5</version>
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
Validator.fromSchema(jsonSchema).validate(json)
//数据验证&数据提取
Validator.fromSchema(jsonSchema).validate(json).extract(json);
```
### 自定义验证扩展

```java
CustomValidationRule customValidationRule = new CustomValidationRule() {
    @Override
    public boolean validate(JsonSchema schema, JsonNode node) throws ValidationException {
        //自定义验证逻辑
        return true;
    }
};

//在 Schema 的任意节点上增加验证策略 withValidator(...) 可选
JsonSchema jsonSchema = JsonObject.required(
    JsonObject.required("item",
        JsonNumber.required("id").withValidator(customValidationRule),
        JsonArray.required("orderIds").withValidator(customValidationRule),
        JsonString.required("title").withValidator(customValidationRule)
    ).withValidator(customValidationRule),
        JsonString.required("name").withValidator(customValidationRule),
        JsonNumber.required("age").withValidator(customValidationRule)
    ).withValidator(customValidationRule);
    //数据验证保持不变
    Validator.fromSchema(jsonSchema).validate(...);

```
### 基于 Spring 请求 body 参数验证示例
演示创建订单接口

#### 订单数据结构
```json 
  {
  "orderId": "ORD123456",
  "user": {
    "userId": "USR78910",
    "name": "张三",
    "email": "zhangsan@example.com",
    "phone": "13800000000"
  },
  "items": [
    {
      "productId": "PROD001",
      "productName": "无线耳机",
      "quantity": 2,
      "price": 199.99,
      "total": 399.98
    },
    {
      "productId": "PROD002",
      "productName": "蓝牙音箱",
      "quantity": 1,
      "price": 299.99,
      "total": 299.99
    }
  ],
  "totalAmount": 699.97,
  "orderDate": "2024-02-04T14:30:00Z",
  "status": "PENDING",
  "shippingAddress": {
    "recipient": "李四",
    "addressLine1": "北京市朝阳区某街道",
    "addressLine2": "小区1号楼",
    "city": "北京",
    "state": "北京市",
    "postalCode": "100000",
    "country": "中国"
  },
  "payment": {
    "method": "CREDIT_CARD",
    "transactionId": "TXN123456789",
    "amount": 699.97,
    "currency": "CNY"
  }
}
```
#### 使用 Json 生成 Schema 代码
```java

CodeGenerationUtils.generateSchemaCode(json, new GenerateOptional());

```    
#### 将生成的 Schema 定义注册到 Spring
```java
    @Bean("orderSchema")
public JsonSchema orderSchema(){
        return JsonObject.required(
            JsonString.required("orderId"),
            JsonObject.required("user",
            JsonString.required("userId"),
        JsonString.required("name"),
        JsonString.required("email"),
        JsonString.required("phone")),
        JsonArray.required("items",
            JsonObject.required(
                JsonString.required("productId"),
                JsonString.required("productName"),
                JsonNumber.required("quantity"),
                JsonNumber.required("price"),
                JsonNumber.required("total"))),
        JsonNumber.required("totalAmount"),
        JsonString.required("orderDate"),
        JsonString.required("status"),
        JsonObject.required("shippingAddress",
        JsonString.required("recipient"),
        JsonString.required("addressLine1"),
        JsonString.required("addressLine2"),
        JsonString.required("city"),
        JsonString.required("state"),
        JsonString.required("postalCode"),
        JsonString.required("country")),
        JsonObject.required("payment",
        JsonString.required("method"),
        JsonString.required("transactionId"),
        JsonNumber.required("amount"),
        JsonString.required("currency")));
        }
```
#### 对 spring controller 请求 body 参数进行验证示例
```java
    @PostMapping("/api/test")
    public ApiResponse<T> createOrder(@RequestBody @JsonSchemaValidate("orderSchema") Order order) {
            //省略代码...  
    }
```

## 许可证

[Apache 2.0 许可证](https://www.apache.org/licenses/LICENSE-2.0)

版权所有 (c) 2024 json-schema-validator
