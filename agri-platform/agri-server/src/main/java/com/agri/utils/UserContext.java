package com.agri.utils;

import lombok.Data;

@Data
public class UserContext {
    private Long userId;
    private String username;
    private String role;
    
    private static final ThreadLocal<UserContext> CONTEXT = new ThreadLocal<>();
    
    public static void set(UserContext context) {
        CONTEXT.set(context);
    }
    
    public static UserContext get() {
        return CONTEXT.get();
    }
    
    public static void clear() {
        CONTEXT.remove();
    }
    
    public static Long getCurrentUserId() {
        UserContext context = get();
        return context != null ? context.getUserId() : null;
    }
    
    public static String getCurrentUsername() {
        UserContext context = get();
        return context != null ? context.getUsername() : null;
    }
    
    public static String getCurrentRole() {
        UserContext context = get();
        return context != null ? context.getRole() : null;
    }
    
    public static boolean isAdmin() {
        return "admin".equals(getCurrentRole());
    }
}
