package com.web.billim.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReIssueTokenRequest {
    private String accessToken;
    private String refreshToken;

}
