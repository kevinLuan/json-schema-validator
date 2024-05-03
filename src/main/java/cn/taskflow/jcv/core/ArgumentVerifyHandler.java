package cn.taskflow.jcv.core;

import cn.taskflow.jcv.utils.JsvUtils;

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
        return JsvUtils.formatParamError(path);
    }

    @Override
    public String getTipMissing(String path) {
        return JsvUtils.formatParamMissing(path);
    }
}
