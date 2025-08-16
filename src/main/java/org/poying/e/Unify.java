package org.poying.e;

public class Unify<T> {

    private static final String SUCCESS_CODE = "000000";

    private static final String SUCCESS_MSG = "成功";

    private static final String FAIL_CODE = "999999";

    private static final String NO_LOGIN_CODE = "666666";

    private static final String FAIL_MSG = "失败";

    private String code;

    private String msg;

    private T data;

    public Unify(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> Unify<T> success(T data) {
        return new Unify<>(SUCCESS_CODE, SUCCESS_MSG, data);
    }

    public static <T> Unify<T> error(String msg) {
        return new Unify<>(FAIL_CODE, msg, null);
    }

    public static <T> Unify<T> noLogin() {
        return new Unify<>(NO_LOGIN_CODE, "未登录", null);
    }

}
