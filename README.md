
## Architecture

![Architecture](...)

## Features
* 根据自定义的结构化参数模型进行合法性验证验证
* 根据定义参数结构提取有效数据结构体，忽略未定义的参数； 

## Getting started

The following code snippet comes from [Json-schema-validator Samples](https://github.com/kevinLuan/json-schema-validator.git). You may clone the sample project.

```bash
git clone https://github.com/kevinLuan/json-schema-validator.git
```

There's a [README](https://github.com/kevinLuan/json-schema-validator/blob/feature/domain/README.md) file under `json-schema-validator` directory. We recommend referencing the samples in that directory by following the below-mentioned instructions:

### Maven dependency
```xml
<dependency>
    <groupId>cn.taskflow.jsv</groupId>
    <artifactId>json-schema-validator</artifactId>
    <version>0.0.2</version>
</dependency>
```


### 应用示例

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
#### 运行以下代码生成JAVA代码
```java
    String code = GeneratorCode.generateCode(json, DefaultMustSchema.INSTANCE);
    System.out.println(code);
```
##### 生成后的代码如下：

```Java
    JsonObject.required(
        JsonObject.required("item", null,
            JsonNumber.required("id", null).setExampleValue(1000),
            JsonArray.required("orderIds", null, JsonNumber.make().setExampleValue(1)),
            JsonString.required("title", null).setExampleValue("item name")
        ),
        JsonString.required("name", null).setExampleValue("张三丰"),
        JsonNumber.required("age", null).setExampleValue(60)
    );    
```
#### 完整示例如下：

```java
    // 请求参数
    Map<String, Object> map = new HashMap<>();
    map.put("name", "张三丰");
    map.put("age", 60);
    map.put("item", new HashMap<>());
    ((Map<String, Object>) map.get("item")).put("id", 1000);
    ((Map<String, Object>) map.get("item")).put("title", "item name");
    ((Map<String, Object>) map.get("item")).put("orderIds", Lists.newArrayList(1, 2, 3, 4, 5));


    //定义验证规则：json-schema
    JsonObject jsonSchema = JsonObject.required(
        JsonObject.required("item", null,
            JsonNumber.required("id", null).setExampleValue(1000),
            JsonArray.required("orderIds", null,
            JsonNumber.make().setExampleValue(1)
        ),
        JsonString.required("title", null).setExampleValue("item name")),
        JsonString.required("name", null).setExampleValue("张三丰"),
        JsonNumber.required("age", null).setExampleValue(60)
        );
    
        //创建验证器
        Validator validator = Validator.of(jsonSchema);
        
        try {//这里将年龄从Number类型定义，调整为非法数据结构，运行会出现参数错误
            request.put("age", "十八岁");
            validator.validate(request);
            Assert.fail("未出现预期异常");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("`age` parameter error", e.getMessage());
        }
        
        //增加忽略字段
        request.put("ignore_field", "忽略数据");
        //根据定义参数提取数据，自动忽略未定义的数据结构
        Map<String, Object> response = validator.extract(request);
        System.out.println("提取数据：" + JsonUtils.stringify(response));
        //{"name":"张三丰","item":{"id":1000,"orderIds":[1,2,3,4,5],"title":"item name"},"age":"十八岁"}
    
```

##### 生成代码工具API

```java
@Test
public void testGenerateCode() {
        String json = "{\"item\":{\"id\":1000,\"orderIds\":[1,2,3,4,5],\"title\":\"item name\"},\"name\":\"张三丰\",\"age\":60}";
        System.out.println("--根据任意JSON数据化生成java代码：定义属性为必须类型--");
        System.out.println(GeneratorCode.generateJavaCode(json, SchemaOption.REQUIRED));
        System.out.println("--根据任意JSON数据化生成java代码：定义为缺省值--");
        System.out.println(GeneratorCode.generateJavaCode(json, SchemaOption.OPTIONAL));
        JsonSchema jsonSchema = GeneratorCode.generateParamFromJson(json);
        System.out.println("-------------根据参数 schema 生成示例数据------------");
        System.out.println(GeneratorCode.generateSampleData(jsonSchema));
        System.out.println("-------------验证schema参数序列化和反序列------------------");
        String paramDefine = GeneratorCode.serialization(jsonSchema);
        JsonSchema jsonSchema1 = GeneratorCode.deserialization(paramDefine);
}
```



## License

json-schema-validator software is licensed under the Apache License Version 2.0. See the [LICENSE](https://www.apache.org/licenses/LICENSE-2.0) file for details.
