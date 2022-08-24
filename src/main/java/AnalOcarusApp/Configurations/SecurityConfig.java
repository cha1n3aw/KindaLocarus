package AnalOcarusApp.Configurations;

import AnalOcarusApp.Handlers.CustomAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import javax.servlet.ServletException;
import java.util.Set;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig extends GlobalMethodSecurityConfiguration
{
    private final UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService)
    {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
    {
//        http.authorizeRequests().antMatchers("/login").permitAll()
//                .antMatchers("/api/**").hasAuthority("ADMIN")
//                //.hasAnyAuthority("ADMIN", "USER")
//                //.anyRequest().authenticated()
//                //.and().formLogin()
//                //.loginPage("/login")
//                //.usernameParameter("username")
//                //.permitAll()
//                .and()
//                //.rememberMe().key("AbcdEfghIjklmNopQrsTuvXyz_0123456789")
//                //.and()
//                .logout().permitAll();


                http
                .formLogin(withDefaults())
                //.cors().disable()
                //.csrf().disable()

                //.authorizeRequests().antMatchers("/**").permitAll()


                        .authorizeRequests()
                        .antMatchers("/api/users.get", "/api/users.add").hasAuthority("ADMIN")
                        .antMatchers("/api/users.getCurrent").hasAnyAuthority("USER", "ADMIN")
                .and()
//
//
//                        .logout(logout -> logout.logoutUrl("/logout")
//                        .addLogoutHandler((request, response, auth) -> {
//                            try
//                            {
//                                request.logout();
//                            }
//                            catch (ServletException e)
//                            {
//                                //logger.error(e.getMessage());
//                            }
//                        }))


                //.authorizeRequests().antMatchers("/api/**").permitAll()

                //.authorizeRequests()
                //.antMatchers("/api/users.getCurrent")
                //.access("hasRole('ADMIN')")
                //.anyRequest()
                //.permitAll()
                //.and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler());

        http.headers().frameOptions().sameOrigin();
        http.authenticationProvider(authenticationProvider());
        return http.build();
        /** TODO: SESSION TIMEOUTS & LOGOUT HANDLER */
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler()
    {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception
    {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public Authentication authentication()
    {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider()
    {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}