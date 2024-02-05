package io.github.jcv.utils;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-02-05
 */
public class JsvUtils {
    public static String formatStringArgs(String name) {
        return name == null ? null : "\"" + name + "\"";
    }
}
