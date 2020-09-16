package cn.turing.firecontrol.common.validator.constraint;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @Description 校验可选项
 * @Author hanyong
 * @Date 2019/07/08 16:50
 * @Version V1.0
 */
public class ValueInValidator implements ConstraintValidator<ValueIn,String> {

    private String[] options;

    @Override
    public void initialize(ValueIn constraintAnnotation) {
        this.options = constraintAnnotation.options();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(options == null || options.length == 0){
            return true;
        }
        if(value == null){
            return false;
        }
        for(String s: options){
            if(s.equals(value)){
                return true;
            }
        }
        return false;
    }
}
