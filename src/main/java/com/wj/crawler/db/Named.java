package com.wj.crawler.db;

import javax.inject.Qualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by SYu on 3/16/2017.
 */
@Qualifier
@Documented
@Retention(RUNTIME)
public @interface Named {
    String value() default "";
}