package com.auction.system.modules.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record KycSubmissionRequest(
        @NotBlank @Pattern(regexp = "[0-9]{9,12}", message = "Số giấy tờ phải gồm 9 đến 12 chữ số") String identityNumber,
        @NotBlank @Size(max = 2048) String identityDocumentUrl
) { }
