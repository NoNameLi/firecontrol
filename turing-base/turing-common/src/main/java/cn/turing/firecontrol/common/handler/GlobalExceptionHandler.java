/*
 *  Copyright (C) 2018  Wanghaobin<463540703@qq.com>

 *  AG-Enterprise 企业版源码
 *  郑重声明:
 *  如果你从其他途径获取到，请告知老A传播人，奖励1000。
 *  老A将追究授予人和传播人的法律责任!

 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package cn.turing.firecontrol.common.handler;

import cn.turing.firecontrol.common.constant.RestCodeConstants;
import cn.turing.firecontrol.common.exception.auth.ClientTokenException;
import cn.turing.firecontrol.common.exception.auth.NonLoginException;
import cn.turing.firecontrol.common.exception.auth.UserInvalidException;
import cn.turing.firecontrol.common.exception.base.BusinessException;
import cn.turing.firecontrol.common.exception.base.ParamErrorException;
import cn.turing.firecontrol.common.msg.BaseResponse;
import cn.turing.firecontrol.core.exception.BaseException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 全局异常拦截处理器
 * @author ace
 * @version 2017/9/8
 */
@ControllerAdvice("cn.turing.firecontrol")
@ResponseBody
public class GlobalExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BaseException.class)
    public BaseResponse baseExceptionHandler(HttpServletResponse response, BaseException ex) {
        logger.error(ex.getMessage(),ex);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new BaseResponse(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public BaseResponse otherExceptionHandler(HttpServletResponse response, Exception ex) {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        logger.error(ex.getMessage(),ex);
        return new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
    }

    @ExceptionHandler(ClientTokenException.class)
    public BaseResponse clientTokenExceptionHandler(HttpServletResponse response, ClientTokenException ex) {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        logger.error(ex.getMessage(),ex);
        return new BaseResponse(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(NonLoginException.class)
    public BaseResponse userTokenExceptionHandler(HttpServletResponse response, NonLoginException ex) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        logger.error(ex.getMessage(),ex);
        return new BaseResponse(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(UserInvalidException.class)
    public BaseResponse userInvalidExceptionHandler(HttpServletResponse response, UserInvalidException ex) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        logger.error(ex.getMessage(),ex);
        return new BaseResponse(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandler(HttpServletResponse response, BusinessException ex) {
        response.setStatus(HttpStatus.METHOD_FAILURE.value());
        logger.info(ex.getMessage(),ex);
        return new BaseResponse(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(ParamErrorException.class)
    public BaseResponse paramErrorExceptionHandler(HttpServletResponse response, ParamErrorException ex) {
        response.setStatus(HttpStatus.OK.value());
        logger.info(ex.getMessage(),ex);
        return new BaseResponse(ex.getStatus(), ex.getMessage());
    }

    /**
     * JSR303  controler参数验证异常处理
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler({BindException.class})
    public BaseResponse  handleBindException(BindException e, HttpServletRequest request) {

        logger.error(ExceptionUtils.getStackTrace(e));//记录完整错误信息
        List<FieldError> errors = e.getBindingResult().getFieldErrors();
//        String msg = ((FieldError)err).getDefaultMessage();
// String msg = err.getDefaultMessage();
          String msg =errors.get(0).getDefaultMessage();
        return new BaseResponse(500,msg);
    }

    @ExceptionHandler( MethodArgumentNotValidException.class)
    public BaseResponse  handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, HttpServletRequest request){
        logger.error(e.getMessage(),e);
        List<ObjectError>  errors=   e.getBindingResult().getAllErrors();
         String msg=  errors.get(0).getDefaultMessage();
        logger.error(msg);
        return new BaseResponse(500,msg);
    }


    //@ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeException(HttpServletResponse response, RuntimeException ex){
        response.setStatus(HttpStatus.OK.value());
        logger.info(ex.getMessage(),ex);
        return new BaseResponse(RestCodeConstants.EX_BUSINESS_BASE_CODE, ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public BaseResponse runtimeException(HttpServletResponse response, HttpMessageNotReadableException ex){
        response.setStatus(HttpStatus.OK.value());
        logger.info(ex.getMessage(),ex);
        return new BaseResponse(RestCodeConstants.EX_BUSINESS_BASE_CODE, "参数错误");
    }

}
