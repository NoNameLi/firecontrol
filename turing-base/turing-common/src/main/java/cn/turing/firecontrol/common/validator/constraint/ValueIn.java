package cn.turing.firecontrol.common.validator.constraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @Description 定义枚举检验
 * @Author hanyong
 * @Date 2019/07/08 16:45
 * @Version V1.0
 */
@Target({ FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = ValueInValidator.class)
@Documented
public @interface ValueIn {

    String message() default "the value is not in options";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    String[] options();
}
