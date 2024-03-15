package uj.wmii.pwj.anns;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MyTest {
    int[] intExpected() default{};
    int[] intParams() default{};
    String[] strExpected() default{};
    String[] strParams() default{};
}