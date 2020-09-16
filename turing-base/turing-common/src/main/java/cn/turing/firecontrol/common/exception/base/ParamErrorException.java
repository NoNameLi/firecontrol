package cn.turing.firecontrol.common.exception.base;

import cn.turing.firecontrol.common.constant.RestCodeConstants;
import cn.turing.firecontrol.core.exception.BaseException;

public class ParamErrorException extends BaseException{

    public ParamErrorException(String message) {
        super(message, RestCodeConstants.EX_PARAM_ERROR_CODE);
    }

}
