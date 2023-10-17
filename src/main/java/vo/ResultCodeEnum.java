package vo;


/**
 * @author zhenqinl
 */
public enum ResultCodeEnum {

    SUCCESS(200, "成功"),

    UNKNOWN_REASON(500, "未知错误");

    private final Integer code;

    private final String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ResultCodeEnum{" + "code=" + code + ", message='" + message + '\'' + '}';
    }
}
