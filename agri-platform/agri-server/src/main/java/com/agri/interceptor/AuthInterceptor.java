package com.agri.interceptor;

import com.agri.annotation.RequireAdmin;
import com.agri.annotation.RequireLogin;
import com.agri.utils.JwtUtil;
import com.agri.utils.UserContext;
import com.agri.vo.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequireLogin requireLogin = handlerMethod.getMethodAnnotation(RequireLogin.class);
        RequireAdmin requireAdmin = handlerMethod.getMethodAnnotation(RequireAdmin.class);
        
        if (requireLogin == null) {
            requireLogin = handlerMethod.getBeanType().getAnnotation(RequireLogin.class);
        }
        if (requireAdmin == null) {
            requireAdmin = handlerMethod.getBeanType().getAnnotation(RequireAdmin.class);
        }
        
        if (requireLogin == null && requireAdmin == null) {
            return true;
        }
        
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            log.warn("请求缺少Authorization头: {}", request.getRequestURI());
            writeError(response, 401, "请先登录");
            return false;
        }
        
        token = token.replace("Bearer ", "");
        
        if (!jwtUtil.validateToken(token)) {
            log.warn("Token验证失败: {}", request.getRequestURI());
            writeError(response, 401, "登录已过期，请重新登录");
            return false;
        }
        
        UserContext context = new UserContext();
        context.setUserId(jwtUtil.getUserId(token));
        context.setUsername(jwtUtil.getUsername(token));
        context.setRole(jwtUtil.getRole(token));
        UserContext.set(context);
        
        if (requireAdmin != null && !"admin".equals(context.getRole())) {
            log.warn("权限不足: userId={}, role={}, uri={}", context.getUserId(), context.getRole(), request.getRequestURI());
            writeError(response, 403, "无权访问");
            return false;
        }
        
        log.debug("认证成功: userId={}, username={}, role={}", context.getUserId(), context.getUsername(), context.getRole());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
    
    private void writeError(HttpServletResponse response, int code, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(code);
        Result<?> result = Result.error(code, message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
