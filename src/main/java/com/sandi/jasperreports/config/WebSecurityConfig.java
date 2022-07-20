package com.sandi.jasperreports.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf()
                .disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .authorizeRequests()
                //.antMatchers("/*").hasAuthority("VIEW_REPORT")
                .antMatchers("/login").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .exceptionHandling().disable().oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
    }

    @Bean
    @Override
    protected UserDetailsService userDetailsService(){
        List<String> rights = new ArrayList<>();
        rights.add("VIEW_USER");
        rights.add("VIEW_REPORT");
        UserDetails userDetails = User
                .withUsername("user")
                .authorities(getGrantedAuthorities(rights))
                .passwordEncoder(passwordEncoder::encode)
                .password("123456")
                .build();

        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(userDetails);
        return manager;
    }

    private Collection<? extends GrantedAuthority> getGrantedAuthorities(final List<String> rights) {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        for (final String privilege : rights) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }

}
