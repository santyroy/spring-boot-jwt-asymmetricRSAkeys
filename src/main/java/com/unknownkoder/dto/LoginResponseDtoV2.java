package com.unknownkoder.dto;

import com.unknownkoder.model.ApplicationUser;

public record LoginResponseDtoV2(ApplicationUser user, String jwt, String refreshToken) {
}
