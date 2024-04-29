package cn.taskflow.jcv.core;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-02-04
 */
public class DataVerifyHandler implements VerifyHandler {
    public static VerifyHandler getInstance() {
        return new DataVerifyHandler();
    }

    @Override
    public String getTipError(String path) {
        return "下游服务返回数据错误->`" + path + "`";
    }

    @Override
    public String getTipMissing(String path) {
        return "下游服务返回数据缺失->`" + path + "`";
    }
}
