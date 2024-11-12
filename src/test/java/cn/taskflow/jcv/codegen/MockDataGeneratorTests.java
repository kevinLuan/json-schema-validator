package cn.taskflow.jcv.codegen;

import cn.taskflow.jcv.extension.SchemaOptions;
import org.junit.Test;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-11-12
 */
public class MockDataGeneratorTests {
    @Test
    public void test() {
        String json = MockDataGenerator.getJsonMock(Person.class);
        System.out.println(json);
        String schema = CodeGenerationUtils.generateSchemaCode(json, SchemaOptions.OPTIONAL);
        System.out.println(schema);
    }
}
