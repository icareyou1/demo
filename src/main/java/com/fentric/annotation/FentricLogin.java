package com.fentric.annotation;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FentricLogin {
    String value();
    boolean required() default true;
    String defaultValue() default "";
}
