package cn.taskflow.jcv.utils;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-02-05
 */
public class JsvUtils {
    public static String formatStringArgs(String name) {
        return name == null ? null : "\"" + name + "\"";
    }

    public static IllegalArgumentException throwParamException(String name) {
        throw new IllegalArgumentException(formatParamError(name));
    }

    public static IllegalArgumentException throwMissingParamException(String name) {
        throw new IllegalArgumentException(formatParamMissing(name));
    }

    public static String formatParamError(String path) {
        return "`" + path + "` parameter error";
    }

    public static String formatParamMissing(String path) {
        return "Missing `" + path + "` parameter";
    }

    public static String formatBetween(String path, Number min, Number max) {
        return "`" + path + "` between [" + min + " ~ " + max + "]";
    }

    public static String formatBetweenGtOrEq(String path, Number min) {
        return "`" + path + "` greater than or equal to " + min;
    }

    public static String formatBetweenLtOrEq(String path, Number max) {
        return "`" + path + "` less than or equal to " + max;
    }

    public static String mustBeNumber(String path) {
        return "`" + path + "` It has to be a number";
    }

    public static String formatBetweenLength(String path, Number min, Number max) {
        return "`" + path + "` between character size [ " + min + "~" + max + " ]";
    }

    public static String formatBetweenLengthGtOrEq(String path, Number min) {
        return "`" + path + "` greater than or equal to character size " + min;
    }

    public static String formatBetweenLengthLtOrEq(String path, Number max) {
        return "`" + path + "` less than or equal to character size " + max;
    }

    public static ClassCastException newClassCastException(Class<?> src, Class<?> dest) {
        throw new ClassCastException(src.getName() + " cannot be cast to " + dest.getName());
    }
}
