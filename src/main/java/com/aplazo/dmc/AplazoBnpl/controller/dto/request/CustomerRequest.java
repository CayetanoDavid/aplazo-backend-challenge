package com.aplazo.dmc.AplazoBnpl.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CustomerRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String secondLastName;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$")
    private String dateOfBirth;

}
