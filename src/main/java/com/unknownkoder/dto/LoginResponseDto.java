package com.unknownkoder.dto;

import com.unknownkoder.model.ApplicationUser;

public record LoginResponseDto(ApplicationUser user, String jwt) {
}
