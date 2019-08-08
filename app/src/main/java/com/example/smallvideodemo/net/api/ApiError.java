package com.example.smallvideodemo.net.api;

/**
 * @author weioule
 * @date 2019/8/3.
 */
public final class ApiError extends Throwable {
    //请求成功，服务器返回正常结果
    public static final int HTTP_SUCCESS_CODE = 200;
    public static final String HTTP_SUCCESS_CODE_MSG = "请求成功";
    //请求成功，服务器返回异常结果（提示用户操作信息）
    public static final int HTTP_SUCCESS_CODE_FAILED = -1;

    //请求成功，该接口需要登录
    public static final int HTTP_ERROR_NEED_LOGIN = -2;
    public static final String HTTP_ERROR_NEED_LOGIN_MSG = "请重新登陆";

    //服务接口做其他特殊处理
    public static final int HTTP_ERROR_REQUEST_OTHER = 1001;

    //接口调用正常，服务器没有返回内容
    public static final int HTTP_ERROR_SERVER = 20001;//服务器出错
    public static final String HTTP_ERROR_SERVER_MSG = "服务器出错";

    public static final int HTTP_ERROR_NETWORK = 20006;
    public static final int HTTP_ERROR__NO_NETWORK = 20007;
    public static final String HTTP_ERROR_NETWORK_MSG = "网络异常";
    public static final String HTTP_ERROR_NO_NETWORK_MSG = "暂无网络";


    public int code;
    public String msg;

    public ApiError(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
