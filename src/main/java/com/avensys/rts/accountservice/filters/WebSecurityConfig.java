package com.avensys.rts.accountservice.filters;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;

public class WebSecurityConfig extends WebSecurityConfiguration {

	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
	}

}
