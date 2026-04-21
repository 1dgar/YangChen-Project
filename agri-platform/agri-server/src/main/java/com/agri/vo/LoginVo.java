package com.agri.vo;

import lombok.Data;

@Data
public class LoginVo {
    private Long id;
    private String username;
    private String role;
    private String token;
}
