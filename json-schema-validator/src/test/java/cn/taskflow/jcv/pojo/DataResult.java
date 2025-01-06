/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.taskflow.jcv.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用RPC数据序列化传递对象
 *
 * @author KEVIN LUAN
 */
public class DataResult<T> extends Result {

    /**
     * 解析协议状态码
     */
    @JsonProperty("status")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Status              status = new Status(0, "");
    /**
     * 返回Result
     */
    @JsonProperty("result")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T                   result;
    /**
     * 附加属性/扩展属性
     */
    @JsonProperty("attachment")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Object> attachment;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public static DataResult<Object> success(Object result) {
        DataResult<Object> dataResult = new DataResult<Object>();
        dataResult.setResult(result);
        return dataResult;
    }

    @SuppressWarnings("unchecked")
    public static <T> DataResult<T> success(Object result, Class<T> type) {
        DataResult<T> dataResult = new DataResult<T>();
        dataResult.setResult((T) result);
        return dataResult;
    }

    public static <T> DataResult<T> faild(int errorCode, String errMsg) {
        DataResult<T> dataResult = new DataResult<T>();
        dataResult.setStatus(new Status(errorCode, errMsg));
        return dataResult;
    }

    public static DataResult<Map<String, Object>> make(String key, Object value) {
        DataResult<Map<String, Object>> dataResult = new DataResult<>();
        dataResult.setResult(new HashMap<>());
        dataResult.result.put(key, value);
        return dataResult;
    }

    @SuppressWarnings("unchecked")
    public DataResult<Map<String, Object>> addResult(String key, Object value) {
        Map<String, Object> result = (Map<String, Object>) this.getResult();
        result.put(key, value);
        return (DataResult<Map<String, Object>>) this;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return getStatus().getStatusCode() == 0;
    }

    /**
     * 接口返回错误
     */
    @JsonIgnore
    public boolean isFailed() {
        return !isSuccess();
    }

    public Map<String, Object> getAttachment() {
        return attachment;
    }

    public void addAttachment(String key, Object value) {
        if (attachment == null) {
            this.attachment = new HashMap<>();
        }
        this.attachment.put(key, value);
    }

    public void setAttachment(Map<String, Object> attachment) {
        this.attachment = attachment;
    }

    public DataResult() {
    }
}