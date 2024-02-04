package io.github.jcv.codec;


public interface Encoder {
    /**
     * 序列化API
     *
     * @param t
     * @return
     */
    String encode(Object t);

    /**
     * 反序列化API
     *
     * @param data
     * @param type
     * @return
     */
    <T> T decode(String data, Class<T> type);
}
