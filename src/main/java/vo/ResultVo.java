package vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: 统一返回结果实体
 * @author: zhenqinl
 * @date: 2023/6/13 10:33
 */
@Data
public class ResultVo implements Serializable {

    private Integer code;
    private String message;
    private Object data;

    public void setResultCode(ResultCodeEnum resultCodeEnum) {
        this.code = resultCodeEnum.getCode();
    }

    public static ResultVo success() {
        ResultVo result = new ResultVo();
        result.setResultCode(ResultCodeEnum.SUCCESS);
        return result;
    }

    public static ResultVo success(String message) {
        ResultVo result = new ResultVo();
        result.setResultCode(ResultCodeEnum.SUCCESS);
        result.setMessage(message);
        return result;
    }

    public static ResultVo success(Object data) {
        ResultVo result = new ResultVo();
        result.setResultCode(ResultCodeEnum.SUCCESS);
        result.setData(data);
        return result;
    }

    public static ResultVo failure(Integer code, String message) {
        ResultVo result = new ResultVo();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static ResultVo failure(String message) {
        ResultVo result = new ResultVo();
        result.setCode(ResultCodeEnum.UNKNOWN_REASON.getCode());
        result.setMessage(message);
        return result;
    }
}

