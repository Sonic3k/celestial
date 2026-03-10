package com.sonic.celestial.common;

public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String error;

    public static <T> ApiResponse<T> ok(T data) {
        ApiResponse<T> r = new ApiResponse<>();
        r.success = true;
        r.data = data;
        return r;
    }

    public static <T> ApiResponse<T> fail(String error) {
        ApiResponse<T> r = new ApiResponse<>();
        r.success = false;
        r.error = error;
        return r;
    }

    public boolean isSuccess() { return success; }
    public T getData() { return data; }
    public String getError() { return error; }
}