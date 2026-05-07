package com.stiven.franchise_api.application.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStockRequest {

    @Min(value = 0, message = "Stock must be zero or positive")
    private int stock;
}
