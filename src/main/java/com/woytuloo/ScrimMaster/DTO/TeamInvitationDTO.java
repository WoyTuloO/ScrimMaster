package com.woytuloo.ScrimMaster.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeamInvitationDTO {
    private Long id;
    private String teamName;
    private String captainName;
}