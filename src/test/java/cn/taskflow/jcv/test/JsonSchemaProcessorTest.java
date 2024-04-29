package cn.taskflow.jcv.test;

import cn.taskflow.jcv.core.*;
import cn.taskflow.jcv.utils.JsonSchemaProcessor;
import org.junit.Test;

public class JsonSchemaProcessorTest {
    private static JsonSchema buildResult() {
        return JsonObject.optional("result", "返回数据", //
                JsonString.required("name", "姓名").setMax(5), //
                JsonNumber.required("age", "年龄").setMin(0).setMax(120), //
                JsonArray.required("items", "商品列表", //
                        JsonObject.required(//
                                JsonNumber.required("id", "商品ID").setMin(1).setMax(10), //
                                JsonString.required("name", "商品名称").setMax(50)//
                        )//
                ), //
                JsonArray.required("ids", "id列表", //
                        JsonNumber.make().setMax(100) //
                )//
        );
    }

    @Test
    public void test() {
        new JsonSchemaProcessor(buildResult()).run((jsonSchema) -> {
            System.out.println("节点名称:" + jsonSchema.getName() + "\t是否必须:" + jsonSchema.isRequired() + "-\t路径：" + jsonSchema.getPath());
        });
    }
}
