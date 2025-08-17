package org.poying.config;

import org.poying.service.UserService;
import org.poying.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security配置类
 * 用于替换默认的登录页面为自定义的login.html
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserService userService;

    /**
     * 配置密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置用户详情服务，使用我们自己的UserService
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            // 使用我们自己的UserService查询用户
            User user = userService.findByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("用户不存在: " + username);
            }
            
            // 将我们的User对象转换为Spring Security的UserDetails
            return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("ADMIN") // 这里可以根据实际情况设置用户角色
                .build();
        };
    }

    /**
     * 配置认证提供者
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * 配置认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * 配置安全过滤器链
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 配置认证提供者
            .authenticationProvider(authenticationProvider())
            // 配置请求授权规则
            .authorizeHttpRequests(authorize -> authorize
                // 允许所有用户访问静态资源和登录相关页面
                .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/img/**", "/lib/**", "/login.html", "/index.html", "/favicon.ico").permitAll()
                // 允许访问登录接口
                .requestMatchers("/api/user/login").permitAll()
                // 允许访问任务API
                .requestMatchers("/api/tasks/**", "/api/jobs/**").authenticated()
                // 其他所有请求都需要认证
                .anyRequest().authenticated()
            )
            // 配置自定义登录页面
            .formLogin(form -> form
                // 设置自定义登录页面
                .loginPage("/login.html")
                // 禁用Spring Security的默认登录处理，使用我们自己的控制器
                .disable()
            )
            // 配置会话管理
            .sessionManagement(session -> session
                // 设置会话过期时间为30分钟
                .invalidSessionUrl("/login.html")
                // 只允许一个会话
                .maximumSessions(1)
                // 当达到最大会话数时，阻止新登录
                .maxSessionsPreventsLogin(false)
            )
            // 配置注销功能
            .logout(logout -> logout
                // 设置注销URL
                .logoutUrl("/logout")
                // 设置注销成功后重定向的URL
                .logoutSuccessUrl("/login.html")
                // 清除会话
                .invalidateHttpSession(true)
                // 清除认证信息
                .clearAuthentication(true)
                .permitAll()
            )
            // 禁用CSRF保护（仅用于开发环境，生产环境应启用）
            .csrf(csrf -> csrf.disable())
            // 配置异常处理
            .exceptionHandling(exception -> exception
                // 处理未认证请求
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendRedirect("/login.html");
                })
                // 处理权限不足请求
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(403);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"权限不足，请重新登录\"}");
                })
            );

        return http.build();
    }
}