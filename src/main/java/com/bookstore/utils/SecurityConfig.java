package com.bookstore.utils;

import com.bookstore.entity.CustomUserDetail;
import com.bookstore.entity.User;
import com.bookstore.repository.IUserRepository;
import com.bookstore.services.CustomUserDetailServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public UserDetailsService userDetailsService(){
        return new CustomUserDetailServices();
    }

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    @Lazy
    private CustomUserDetailServices customUserDetailsService;

    private final DataSource dataSource;

    @Autowired
    public SecurityConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler(){
        return (request,response, accessDeniedException) -> response.sendRedirect(request.getContextPath() + "/error/403");
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService());
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws
            Exception {
        return http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers( "/css/**", "/js/**", "/", "/register",
                                "/error")
                        .permitAll()
                        .requestMatchers("/submit_recovery_password/**")
                        .permitAll()
                        .requestMatchers("/recovery_password")
                        .permitAll()
                        .requestMatchers( "/books/edit", "/books/delete", "/users/**")
                        .hasAnyAuthority("ADMIN")
                        .requestMatchers("/books", "/books/add")
                        .hasAnyAuthority("ADMIN", "USER")
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout.logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                )
                .formLogin(formLogin -> formLogin.loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/")
                        .permitAll()
                )
//                .oauth2Login(oauth2Login -> oauth2Login
//                        .loginPage("/login")
//                        .defaultSuccessUrl("/")
//                        .permitAll()
//                )
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/login")
                        .clientRegistrationRepository(clientRegistrationRepository())
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userService(oauth2UserService()))
                )
                .rememberMe(rememberMe -> rememberMe.key("uniqueAndSecret")
                        .tokenValiditySeconds(86400)
                        .userDetailsService(userDetailsService())
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.accessDeniedHandler(customAccessDeniedHandler()))
                .build();
    }
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = Arrays.asList(
                googleClientRegistration()
        );
        return new InMemoryClientRegistrationRepository(registrations);
    }

    private ClientRegistration googleClientRegistration() {
        return CommonOAuth2Provider.GOOGLE.getBuilder("google")
                .clientId("1094118264922-622e85rqng6kd6pc93o29cjs88cd8qn4.apps.googleusercontent.com")
                .clientSecret("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")
                .scope("profile", "email")
                .build();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        return new DefaultOAuth2UserService() {
            @Override
            public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
                OAuth2User oAuth2User = super.loadUser(userRequest);
                // Process OAuth2 user and save/update to database
                User user = customUserDetailsService.processOAuth2User(oAuth2User);
                CustomUserDetail customUserDetail = new CustomUserDetail(user,userRepository);

                OAuth2User oauth2User = new DefaultOAuth2User(
                        // Provide authorities if needed
                        customUserDetail.getAuthorities(),
                        // Attributes (name, email, etc.)
                        oAuth2User.getAttributes(),
                        // Unique identifier from the OAuth2 provider
                        "sub"
                );

                // Return the OAuth2User as UserDetails
                return oauth2User;
            }
        };
    }
}
