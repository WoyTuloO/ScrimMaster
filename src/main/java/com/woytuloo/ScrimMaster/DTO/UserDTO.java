package com.woytuloo.ScrimMaster.DTO;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private double kd;
    private double adr;
    private int ranking;
    private String role;
}
