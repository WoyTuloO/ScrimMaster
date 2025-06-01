package com.woytuloo.ScrimMaster.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordEntity {
    private String currentPassword;
    private String newPassword;

}
