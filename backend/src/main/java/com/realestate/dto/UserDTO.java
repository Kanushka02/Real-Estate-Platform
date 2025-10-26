package com.realestate.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private Set<String> roles;
    private Boolean active;
    private LocalDateTime createdAt;
}

