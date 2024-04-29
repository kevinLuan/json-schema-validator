package cn.taskflow.jcv.test;

import cn.taskflow.jcv.core.JsonBoolean;
import cn.taskflow.jcv.core.JsonObject;
import com.github.javaparser.StaticJavaParser;
import cn.taskflow.jcv.core.JsonArray;
import cn.taskflow.jcv.core.JsonSchema;
import cn.taskflow.jcv.utils.CodeGenerator;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-02-04
 */
public class CodeGeneratorTest {
    @Test
    public void test() {
        String json = "{" +
                "    'b':true," +
                "    'a':false," +
                "    'bools':[" +
                "        true," +
                "        false" +
                "    ]" +
                "}";
        String javaCode = CodeGenerator.generateCode(json);
        String expected = String.valueOf("JsonObject.optional(" +
                "   JsonBoolean.optional('b', null).setExampleValue(true)," +
                "   JsonBoolean.optional('a', null).setExampleValue(false)," +
                "   JsonArray.optional('bools', null, JsonBoolean.make().setExampleValue(true))" +
                ");").replace('\'', '"');
        System.out.println("格式化代码：" + StaticJavaParser.parseStatement(javaCode));
        System.out.println("原始代码:" + StaticJavaParser.parseStatement(expected).toString());
        Assert.assertEquals(StaticJavaParser.parseStatement(expected).toString(), StaticJavaParser.parseStatement(javaCode).toString());

        JsonSchema jsonSchema = JsonObject.optional(
                JsonBoolean.optional("b", null).setExampleValue("true"),
                JsonBoolean.optional("a", null).setExampleValue("false"),
                JsonArray.optional("bools", null, JsonBoolean.make().setExampleValue("true"))
        );
        System.out.println(CodeGenerator.generateSampleData(jsonSchema));
        System.out.println(CodeGenerator.serialization(jsonSchema));
    }
}
