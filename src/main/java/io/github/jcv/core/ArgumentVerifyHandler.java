package io.github.jcv.core;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-02-04
 */
public class ArgumentVerifyHandler implements VerifyHandler {
    public static VerifyHandler getInstance() {
        return new ArgumentVerifyHandler();
    }

    @Override
    public String getTipError(String path) {
        return "`" + path + "`参数错误";
    }

    @Override
    public String getTipMissing(String path) {
        return "`" + path + "`参数缺失";
    }
}
