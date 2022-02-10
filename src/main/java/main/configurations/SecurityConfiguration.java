package main.configurations;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/registration").permitAll()
                /*.antMatchers().hasAuthority("user")*/
                /*.antMatchers().hasAuthority("admin")*/
                .antMatchers("/").authenticated()
                .and()
                .formLogin().loginPage("/login")
                .and()
                .logout();
    }
}
