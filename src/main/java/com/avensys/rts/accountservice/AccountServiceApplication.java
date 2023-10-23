package com.avensys.rts.accountservice;

import com.avensys.rts.accountservice.interceptor.AuditInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@SpringBootApplication
@EnableFeignClients
@EnableAspectJAutoProxy
public class AccountServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(AccountServiceApplication.class, args);
	}

}
