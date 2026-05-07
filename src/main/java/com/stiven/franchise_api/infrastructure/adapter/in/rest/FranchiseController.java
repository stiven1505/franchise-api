package com.stiven.franchise_api.infrastructure.adapter.in.rest;

import com.stiven.franchise_api.application.dto.BranchRequest;
import com.stiven.franchise_api.application.dto.FranchiseRequest;
import com.stiven.franchise_api.application.dto.ProductRequest;
import com.stiven.franchise_api.application.dto.TopStockProductResponse;
import com.stiven.franchise_api.application.dto.UpdateNameRequest;
import com.stiven.franchise_api.application.dto.UpdateStockRequest;
import com.stiven.franchise_api.domain.model.Franchise;
import com.stiven.franchise_api.domain.port.in.FranchiseUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/franchises")
@RequiredArgsConstructor
public class FranchiseController {

    private final FranchiseUseCase franchiseUseCase;

    // 1. Create franchise
    @PostMapping
    public Mono<ResponseEntity<Franchise>> createFranchise(@Valid @RequestBody FranchiseRequest request) {
        return franchiseUseCase.createFranchise(request)
                .map(franchise -> ResponseEntity.status(HttpStatus.CREATED).body(franchise));
    }

    // 2. Add branch to franchise
    @PostMapping("/{franchiseId}/branches")
    public Mono<ResponseEntity<Franchise>> addBranch(@PathVariable String franchiseId,
                                                      @Valid @RequestBody BranchRequest request) {
        return franchiseUseCase.addBranch(franchiseId, request)
                .map(ResponseEntity::ok);
    }

    // 3. Add product to branch
    @PostMapping("/{franchiseId}/branches/{branchId}/products")
    public Mono<ResponseEntity<Franchise>> addProduct(@PathVariable String franchiseId,
                                                       @PathVariable String branchId,
                                                       @Valid @RequestBody ProductRequest request) {
        return franchiseUseCase.addProduct(franchiseId, branchId, request)
                .map(ResponseEntity::ok);
    }

    // 4. Remove product from branch
    @DeleteMapping("/{franchiseId}/branches/{branchId}/products/{productId}")
    public Mono<ResponseEntity<Franchise>> removeProduct(@PathVariable String franchiseId,
                                                          @PathVariable String branchId,
                                                          @PathVariable String productId) {
        return franchiseUseCase.removeProduct(franchiseId, branchId, productId)
                .map(ResponseEntity::ok);
    }

    // 5. Update product stock
    @PatchMapping("/{franchiseId}/branches/{branchId}/products/{productId}/stock")
    public Mono<ResponseEntity<Franchise>> updateProductStock(@PathVariable String franchiseId,
                                                               @PathVariable String branchId,
                                                               @PathVariable String productId,
                                                               @Valid @RequestBody UpdateStockRequest request) {
        return franchiseUseCase.updateProductStock(franchiseId, branchId, productId, request)
                .map(ResponseEntity::ok);
    }

    // 6. Get top stock product per branch
    @GetMapping("/{franchiseId}/top-stock-products")
    public Flux<TopStockProductResponse> getTopStockProducts(@PathVariable String franchiseId) {
        return franchiseUseCase.getTopStockProducts(franchiseId);
    }

    // 7. Update franchise name
    @PatchMapping("/{franchiseId}/name")
    public Mono<ResponseEntity<Franchise>> updateFranchiseName(@PathVariable String franchiseId,
                                                                @Valid @RequestBody UpdateNameRequest request) {
        return franchiseUseCase.updateFranchiseName(franchiseId, request)
                .map(ResponseEntity::ok);
    }

    // 8. Update branch name
    @PatchMapping("/{franchiseId}/branches/{branchId}/name")
    public Mono<ResponseEntity<Franchise>> updateBranchName(@PathVariable String franchiseId,
                                                             @PathVariable String branchId,
                                                             @Valid @RequestBody UpdateNameRequest request) {
        return franchiseUseCase.updateBranchName(franchiseId, branchId, request)
                .map(ResponseEntity::ok);
    }

    // 9. Update product name
    @PatchMapping("/{franchiseId}/branches/{branchId}/products/{productId}/name")
    public Mono<ResponseEntity<Franchise>> updateProductName(@PathVariable String franchiseId,
                                                              @PathVariable String branchId,
                                                              @PathVariable String productId,
                                                              @Valid @RequestBody UpdateNameRequest request) {
        return franchiseUseCase.updateProductName(franchiseId, branchId, productId, request)
                .map(ResponseEntity::ok);
    }
}
