package com.flyxia.flytalk.vo;

/**
 * @author automannn@163.com
 * @time 2019/4/25 17:46
 */
public class Result {
    private boolean isSuccess;
    private Object data;

    public Result(boolean isSuccess, Object data) {
        this.isSuccess = isSuccess;
        this.data = data;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        this.isSuccess = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
