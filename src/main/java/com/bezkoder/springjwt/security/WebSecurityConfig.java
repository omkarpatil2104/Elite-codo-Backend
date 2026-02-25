package com.bezkoder.springjwt.security;

import com.bezkoder.springjwt.security.jwt.JwtUtils;
import com.bezkoder.springjwt.services.impl.DeviceManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.bezkoder.springjwt.security.jwt.AuthEntryPointJwt;
import com.bezkoder.springjwt.security.jwt.AuthTokenFilter;
import com.bezkoder.springjwt.security.services.UserDetailsServiceImpl;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
//import org.springframework.security.web.firewall.StrictHttpFirewall;
//import org.springframework.security.web.firewall.DefaultHttpFirewall;

@Configuration
@EnableMethodSecurity
// (securedEnabled = true,
// jsr250Enabled = true,
// prePostEnabled = true) // by default
public class WebSecurityConfig { // extends WebSecurityConfigurerAdapter {
  @Autowired
  UserDetailsServiceImpl userDetailsService;

  @Autowired
  private AuthEntryPointJwt unauthorizedHandler;

  @Autowired
  private JwtUtils jwtUtils;
  @Autowired
  private DeviceManagementService deviceService;

  @Bean
  public AuthTokenFilter authenticationJwtTokenFilter() {
    return new AuthTokenFilter();
  }

//  @Override
//  public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
//    authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
//  }
  
  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
      DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
       
      authProvider.setUserDetailsService(userDetailsService);
      authProvider.setPasswordEncoder(passwordEncoder());
   
      return authProvider;
  }

//  @Bean
//  @Override
//  public AuthenticationManager authenticationManagerBean() throws Exception {
//    return super.authenticationManagerBean();
//  }

  
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder  passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

//  @Override
//  protected void configure(HttpSecurity http) throws Exception {
//    http.cors().and().csrf().disable()
//      .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
//      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
//      .authorizeRequests().antMatchers("/api/auth/**").permitAll()
//      .antMatchers("/api/test/**").permitAll()
//      .anyRequest().authenticated();
//
//    http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
//  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
//    http.cors(httpSecurityCorsConfigurer ->
//            httpSecurityCorsConfigurer.configurationSource(new CorsConfigurationSource() {
//              @Override
//              public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
//                CorsConfiguration corsConfiguration = new CorsConfiguration();
//                corsConfiguration.addAllowedOrigin("*");
////                  corsConfiguration.setAllowedOrigins(Arrays.asList("*")); // allow all origins
////                  corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
////                  corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
////                  corsConfiguration.setAllowCredentials(true);
//                  return corsConfiguration;
//              }
//            }));
    http.csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .antMatchers("/api/auth/**").permitAll()
                    .antMatchers("/api/file/**").permitAll()
                    .antMatchers("/api/subject/**").permitAll()
                    .antMatchers("/api/standard/**").permitAll()
                    .antMatchers("/api/entranceexam/**").permitAll()
                    .antMatchers("/api/chapter/**").permitAll()
                    .antMatchers("/api/testtype/**").permitAll()
                    .antMatchers("/api/question/**").permitAll()
                    .antMatchers("/api/topic/**").permitAll()
                    .antMatchers("/api/subtopic/**").permitAll()
                    .antMatchers("/api/yearofappearance/**").permitAll()
                    .antMatchers("/api/questiontype/**").permitAll()
                    .antMatchers("/api/questionlevel/**").permitAll()
                    .antMatchers("/api/test/**").permitAll()
                    .antMatchers("/api/pattern/**").permitAll()
                    .antMatchers("/api/**").permitAll()
                    .antMatchers("/api/testing/**").permitAll()
                    .antMatchers("/api/theme/**").permitAll()
                    .antMatchers("/api/package/**").permitAll()
                    .antMatchers("/api/chapterweightage/**").permitAll()
                    .antMatchers("/api/institute/**").permitAll()
                    .antMatchers("/api/**").permitAll()
                    .antMatchers("/**").permitAll()
                    .anyRequest().authenticated()
            );

    http.authenticationProvider(authenticationProvider());

    http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(new DeviceSessionFilter(jwtUtils, deviceService),
                    AuthTokenFilter.class);;

    return http.build();
  }

//  @Bean
//  public HttpFirewall allowNewlineHttpFirewall() {
//    StrictHttpFirewall firewall = new StrictHttpFirewall();
//    firewall.setAllowUrlEncodedNewline(true); // Allow "%0A" in URLs
//    return firewall;
//  }
//
//  @Bean
//  public org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer webSecurityCustomizer() {
//    return web -> web.httpFirewall(allowNewlineHttpFirewall());
//  }

  @Bean
  public HttpFirewall defaultHttpFirewall() {
    return new DefaultHttpFirewall();
  }

}
