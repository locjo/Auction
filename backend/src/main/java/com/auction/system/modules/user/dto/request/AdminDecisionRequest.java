package com.auction.system.modules.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminDecisionRequest(
        @NotBlank @Size(max = 500) String reason
) { }
