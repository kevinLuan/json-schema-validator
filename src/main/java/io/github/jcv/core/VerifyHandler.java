package io.github.jcv.core;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-02-04
 */
public interface VerifyHandler {
    String getTipError(String path);

    default IllegalArgumentException throwError(String path) {
        throw new IllegalArgumentException(getTipError(path));
    }

    String getTipMissing(String path);

    default IllegalArgumentException throwMissing(String path) {
        throw new IllegalArgumentException(getTipMissing(path));
    }
}
