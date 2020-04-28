package com.kingja.oksp.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.CLASS)
public @interface SpParam {
    String defString() default "";

    boolean defBoolean() default false;

    float defFloat() default 0f;

    int defInt() default 0;

    long defLong() default 0L;
}
