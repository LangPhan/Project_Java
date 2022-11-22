package com.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

    //AUTHENTICATION
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    //AUTHORIZATION
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests().antMatchers("/user/login", "/user/logout","/user/register","/js/**", "/css/**").permitAll();
        http.authorizeRequests().antMatchers("/user/admin/**").access("hasAuthority('ROLE_ADMIN')");
        http.authorizeRequests().antMatchers("/user/**").hasAnyAuthority("ROLE_ADMIN","ROLE_USER");
        http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/user/404");
        http.authorizeRequests().and().formLogin()
                .loginProcessingUrl("/j_spring_security_check")
                .loginPage("/user/login")
                .successForwardUrl("/user/login")
                .failureForwardUrl("/user/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .and().logout().logoutUrl("/user/logout").logoutSuccessUrl("/user/login").deleteCookies("username")
                .and().rememberMe().tokenValiditySeconds(7 * 24 * 60 * 60).key("this_is_key");
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
