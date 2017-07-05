package org.remipassmoilesel.safranlices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

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
                .loginPage(Mappings.ADMIN_PAGE_LOGIN)
                .loginProcessingUrl(Mappings.ADMIN_PAGE_LOGIN)
                .defaultSuccessUrl(Mappings.ADMIN_PAGE)
                .failureUrl(Mappings.ADMIN_PAGE_LOGIN)
                .permitAll()
                .and()
                .logout()
                .logoutUrl(Mappings.ADMIN_PAGE_LOGOUT)
                .permitAll();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser(adminLogin).password(adminPassword).roles(ADMIN);
    }
}