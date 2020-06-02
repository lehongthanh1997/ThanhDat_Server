package com.bfwg.config;

//import com.bfwg.mail.MailController;
import com.bfwg.model.User;
import com.bfwg.security.TokenHelper;
import com.bfwg.security.auth.RestAuthenticationEntryPoint;
import com.bfwg.security.auth.TokenAuthenticationFilter;
import com.bfwg.service.UserService;
import com.bfwg.service.impl.CustomUserDetailsService;
import com.bfwg.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by fan.jin on 2016-10-19.
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public MailController mailController() {
//        return new MailController();
//    }

    @Autowired
    private CustomUserDetailsService jwtUserDetailsService;

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Bean
    public UserService transactionService() {
        return new UserServiceImpl() {
            @Override
            public User findById(Long id) {
                return null;
            }
        };
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Autowired
    public void configureGlobal( AuthenticationManagerBuilder auth ) throws Exception {
        auth.userDetailsService( jwtUserDetailsService )
            .passwordEncoder( passwordEncoder() );
    }

    @Autowired
    TokenHelper tokenHelper;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy( SessionCreationPolicy.STATELESS ).and()
                .exceptionHandling().authenticationEntryPoint( restAuthenticationEntryPoint ).and()
                .authorizeRequests()
                .antMatchers(
                        HttpMethod.GET,
                        "/",
                        "/auth/**",
                        "/webjars/**",
                        "/*.html",
                        "/favicon.ico",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js"
                ).permitAll()
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/api/user").permitAll()
                .antMatchers("/api/model-car/getAll").permitAll()
                .antMatchers("/api/model-car/getOne/**").permitAll()
                .antMatchers("/api/model-car/**").permitAll()
                .antMatchers("/api/car/**").permitAll()
                .antMatchers("/api/tourType/**").permitAll()
                .antMatchers("/api/tour/**").permitAll()
                .antMatchers("/api/flight/**").permitAll()
                .antMatchers("/api/hotel/**").permitAll()
                .antMatchers("/api/orderTour/**").permitAll()
                .antMatchers("/**").permitAll()
                .antMatchers("/auth/login").permitAll()
                .antMatchers("/auth/set-user").permitAll()
                .anyRequest().authenticated().and()
                .addFilterBefore(new TokenAuthenticationFilter(tokenHelper, jwtUserDetailsService), BasicAuthenticationFilter.class);

        http.csrf().disable();
    }
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH");
            }
        };
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // TokenAuthenticationFilter will ignore the below paths
        web.ignoring().antMatchers(
                HttpMethod.POST,
                "/auth/login"
        );
        web.ignoring().antMatchers(
                HttpMethod.GET,
                "/",
                "/webjars/**",
                "/*.html",
                "/favicon.ico",
                "/**/*.html",
                "/**/*.css",
                "/**/*.js"
            );

    }
}
