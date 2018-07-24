package me.brandon.ai.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigOption
{

	enum DataType
	{
		Unknown, INTEGER, DOUBLE, FLOAT, STRING
	}

	String option() default "";

	DataType type() default DataType.Unknown;

	boolean isArray() default false;
}
