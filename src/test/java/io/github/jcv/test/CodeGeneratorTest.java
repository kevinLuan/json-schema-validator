package io.github.jcv.test;

import com.github.javaparser.StaticJavaParser;
import io.github.jcv.core.JsonArray;
import io.github.jcv.core.JsonBoolean;
import io.github.jcv.core.JsonObject;
import io.github.jcv.core.JsonSchema;
import io.github.jcv.utils.CodeGenerator;
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
                "   JsonBoolean.optional('b', 'null').setExampleValue(true)," +
                "   JsonBoolean.optional('a', 'null').setExampleValue(false)," +
                "   JsonArray.optional('bools', null, JsonBoolean.make().setExampleValue(true))" +
                ");").replace('\'', '"');
        System.out.println(StaticJavaParser.parseStatement(javaCode));
        Assert.assertEquals(StaticJavaParser.parseStatement(expected), StaticJavaParser.parseStatement(javaCode));

        JsonSchema jsonSchema = JsonObject.optional(
                JsonBoolean.optional("b", "null").setExampleValue("true"),
                JsonBoolean.optional("a", "null").setExampleValue("false"),
                JsonArray.optional("bools", null, JsonBoolean.make().setExampleValue("true"))
        );
        System.out.println(CodeGenerator.generateSampleData(jsonSchema));
        System.out.println(CodeGenerator.serialization(jsonSchema));
    }
}
