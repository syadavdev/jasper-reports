package com.sandi.jasperreports;

import org.springframework.boot.test.context.SpringBootTest;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Target(value = TYPE)
@Retention(value = RUNTIME)
@SpringBootTest(classes = JasperReportsApplication.class, webEnvironment = RANDOM_PORT)
public @interface IntegrationTest {
}
