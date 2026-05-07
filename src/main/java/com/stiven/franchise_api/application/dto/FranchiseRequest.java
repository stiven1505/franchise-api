package com.stiven.franchise_api.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseRequest {

    @NotBlank(message = "Franchise name is required")
    private String name;
}
