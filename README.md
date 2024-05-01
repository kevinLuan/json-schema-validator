#### 项目介绍

    1. 根据自定义的结构化参数模型进行合法性验证验证
    2. 根据定义参数结构提取有效数据结构体，忽略未定义的参数；

#### Maven 依赖
```xml
<dependency>
    <groupId>cn.taskflow.jsv</groupId>
    <artifactId>json-schema-validator</artifactId>
    <version>0.0.2</version>
</dependency>
```

##### 定义请求参数对象结构

```Java
    JsonSchema jsonSchema = JsonObject.optional("userInfo","用户信息",
        JsonString.required("name","姓名").setMin(2).setMax(10),//姓名必填,格式: 2~10字符
        JsonNumber.optional("age","年龄").between(18,65)//年龄字段选填，格式: 18岁~65岁
    );
```

##### 参数合法性验证

客户端请求代码示例：

```java
    MockHttpServletRequest request=new MockHttpServletRequest();
    Map<String, Object> map=new HashMap<>();
    map.put("name","张三丰");
    map.put("age","60");
    map.put("sql","CSRF漏洞");
    request.addParameter("userInfo",JsonUtils.stringify(map));
```

客户端请求数据示例：

```json
  {
  "name": "张三",
  "age": 30
  //    ...
}
```

##### 服务端数据验证

代码示例：

```java
    JsonSchema jsonSchema = JsonObject.required("userInfo","用户信息",
        JsonString.required("name","姓名").setMin(2).setMax(32),
        JsonNumber.optional("age",null).between(3,18)
    );
    try{
        Validator validator=Validator.request(jsonSchema);
        //验证请求参数
        validator.checkRequest(request);
        //根据验证参数要求格式，提取数据
        Map<String, Object> extractData=validator.extractRequest(request);
    }catch(IllegalArgumentException e){
        Assert.assertEquals("`paramInfo.age`限制范围3(含)~18(含)",e.getMessage());
    }
```

##### 验证完成并提取合法性数据

代码示例：

```java
    JsonSchema jsonSchema = JsonObject.required("userInfo","用户信息",
        JsonString.required("name","姓名").setMin(2).setMax(32),
        JsonNumber.optional("age",null).between(3,18)
    );
    Map<String, Object> extractData = Validator.request(jsonSchema).checkRequest(request)
        .extractRequest(request);
```

#### 代码生成

##### 根据任意 JSON 数据自动生成 Param 定义

```json
  {
  "name": "张三丰",
  "ids": [
    100
  ],
  "items": [
    {
      "name": "手机",
      "id": 2
    }
  ],
  "age": 100.11
}
```

生成代码工具API

```java
   String javaCode = ParamUtils.generateCode(json);
   System.out.println("生成Param代码:"+javaCode);
   //根据 JSON  数据生成运行时Param对象
    Param jsonSchema=ParamUtils.fromJsonToParam(json);
    //根据 Param 生成数据示例格式
    String dataExample=ParamUtils.toJsonDataExample(jsonSchema);
```

生成代码如下：

```java
JsonObject.optional(
    JsonString.optional("name",null).setExampleValue("张三丰"),
        JsonArray.optional("ids",null,JsonNumber.ofNullable()),
            JsonArray.optional("items",null,
                JsonObject.optional(
                    JsonString.optional("name",null).setExampleValue("手机"),
                    JsonNumber.optional("id",null).setExampleValue(2)
                )
            ),
    JsonNumber.optional("age",null).setExampleValue(100.11)
);
```

##### 根据 Param 定义自动生成原生 JSON 数据，可以用来作为请求示例使用

##### Param 可以支持序列化和反序列能力，用来满足动态配置验证规则场景

