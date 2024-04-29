package cn.taskflow.jcv.encode;

import com.google.gson.Gson;

public class GsonEncoder implements Encoder {

    public static GsonEncoder INSTANCE = new GsonEncoder();

    private static Gson gson = new Gson();

    public String encode(Object t) {
        return gson.toJson(t);
    }

    public <T> T decode(String json, Class<T> clazz) {
        return (T) gson.fromJson(json, clazz);
    }
}
