package com.stiven.franchise_api.domain.port.in;

import com.stiven.franchise_api.application.dto.BranchRequest;
import com.stiven.franchise_api.application.dto.FranchiseRequest;
import com.stiven.franchise_api.application.dto.ProductRequest;
import com.stiven.franchise_api.application.dto.TopStockProductResponse;
import com.stiven.franchise_api.application.dto.UpdateNameRequest;
import com.stiven.franchise_api.application.dto.UpdateStockRequest;
import com.stiven.franchise_api.domain.model.Franchise;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranchiseUseCase {

    Mono<Franchise> createFranchise(FranchiseRequest request);

    Mono<Franchise> addBranch(String franchiseId, BranchRequest request);

    Mono<Franchise> addProduct(String franchiseId, String branchId, ProductRequest request);

    Mono<Franchise> removeProduct(String franchiseId, String branchId, String productId);

    Mono<Franchise> updateProductStock(String franchiseId, String branchId, String productId, UpdateStockRequest request);

    Flux<TopStockProductResponse> getTopStockProducts(String franchiseId);

    Mono<Franchise> updateFranchiseName(String franchiseId, UpdateNameRequest request);

    Mono<Franchise> updateBranchName(String franchiseId, String branchId, UpdateNameRequest request);

    Mono<Franchise> updateProductName(String franchiseId, String branchId, String productId, UpdateNameRequest request);
}
