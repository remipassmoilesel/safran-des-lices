package org.remipassmoilesel.safranlices.config;

import org.remipassmoilesel.safranlices.Mappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Value("${app.admin-login}")
    private String adminLogin;

    @Value("${app.admin-password}")
    private String adminPassword;

    private static final String ANONYMOUS = "ANONYMOUS_ROLE";
    private static final String ADMIN = "ADMIN_ROLE";

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // avoid weird errors
        http.csrf().disable()
                // allow calls from iframes
                .headers().frameOptions().sameOrigin()
                .and()
                // normal visitors are logged as anonymous
                .anonymous()
                .authorities(ANONYMOUS)
                .and()
                .authorizeRequests()
                // restrict admin traffic - must be BEFORE
                .antMatchers(Mappings.ADMIN_PAGE + "/**").hasAnyRole(ADMIN)
                // permit normal traffic
                .antMatchers(Mappings.ROOT + "**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage(Mappings.ADMIN_LOGIN)
                .loginProcessingUrl(Mappings.ADMIN_LOGIN)
                .defaultSuccessUrl(Mappings.ADMIN_PAGE)
                .failureUrl(Mappings.ADMIN_LOGIN)
                .permitAll()
                .and()
                .logout()
                .logoutUrl(Mappings.ADMIN_LOGOUT)
                .permitAll();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //web.debug(true);
        web.debug(false);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser(adminLogin).password(adminPassword).roles(ADMIN);
    }
}