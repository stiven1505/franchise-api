package com.stiven.franchise_api.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopStockProductResponse {

    private String productId;
    private String productName;
    private int stock;
    private String branchId;
    private String branchName;
}
