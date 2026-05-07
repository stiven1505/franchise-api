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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/v1/franchises")
@RequiredArgsConstructor
@Tag(name = "Franchise Management", description = "Operations related to franchises, branches, and products")
public class FranchiseController {

    private final FranchiseUseCase franchiseUseCase;

    // 1. Create franchise
    @PostMapping
    @Operation(summary = "Create a new franchise", description = "Creates a new franchise record in the system.")
    @ApiResponse(responseCode = "201", description = "Franchise created successfully")
    public Mono<ResponseEntity<Franchise>> createFranchise(@Valid @RequestBody FranchiseRequest request) {
        return franchiseUseCase.createFranchise(request)
                .map(franchise -> ResponseEntity.status(HttpStatus.CREATED).body(franchise));
    }

    // 2. Add branch to franchise
    @PostMapping("/{franchiseId}/branches")
    @Operation(summary = "Add a branch to a franchise", description = "Adds a new branch to an existing franchise.")
    @ApiResponse(responseCode = "200", description = "Branch added successfully")
    public Mono<ResponseEntity<Franchise>> addBranch(@PathVariable String franchiseId,
                                                      @Valid @RequestBody BranchRequest request) {
        return franchiseUseCase.addBranch(franchiseId, request)
                .map(ResponseEntity::ok);
    }

    // 3. Add product to branch
    @PostMapping("/{franchiseId}/branches/{branchId}/products")
    @Operation(summary = "Add a product to a branch", description = "Adds a new product to a specific branch within a franchise.")
    @ApiResponse(responseCode = "200", description = "Product added successfully")
    public Mono<ResponseEntity<Franchise>> addProduct(@PathVariable String franchiseId,
                                                       @PathVariable String branchId,
                                                       @Valid @RequestBody ProductRequest request) {
        return franchiseUseCase.addProduct(franchiseId, branchId, request)
                .map(ResponseEntity::ok);
    }

    // 4. Remove product from branch
    @DeleteMapping("/{franchiseId}/branches/{branchId}/products/{productId}")
    @Operation(summary = "Remove a product from a branch", description = "Deletes a product from a specific branch.")
    @ApiResponse(responseCode = "200", description = "Product removed successfully")
    public Mono<ResponseEntity<Franchise>> removeProduct(@PathVariable String franchiseId,
                                                          @PathVariable String branchId,
                                                          @PathVariable String productId) {
        return franchiseUseCase.removeProduct(franchiseId, branchId, productId)
                .map(ResponseEntity::ok);
    }

    // 5. Update product stock
    @PatchMapping("/{franchiseId}/branches/{branchId}/products/{productId}/stock")
    @Operation(summary = "Update product stock", description = "Updates the stock level of a specific product.")
    @ApiResponse(responseCode = "200", description = "Stock updated successfully")
    public Mono<ResponseEntity<Franchise>> updateProductStock(@PathVariable String franchiseId,
                                                               @PathVariable String branchId,
                                                               @PathVariable String productId,
                                                               @Valid @RequestBody UpdateStockRequest request) {
        return franchiseUseCase.updateProductStock(franchiseId, branchId, productId, request)
                .map(ResponseEntity::ok);
    }

    // 6. Get top stock product per branch
    @GetMapping("/{franchiseId}/top-stock-products")
    @Operation(summary = "Get top stock products", description = "Retrieves the product with the highest stock for each branch of a franchise.")
    @ApiResponse(responseCode = "200", description = "List of top stock products")
    public Flux<TopStockProductResponse> getTopStockProducts(@PathVariable String franchiseId) {
        return franchiseUseCase.getTopStockProducts(franchiseId);
    }

    // 7. Update franchise name
    @PatchMapping("/{franchiseId}/name")
    @Operation(summary = "Update franchise name", description = "Updates the name of an existing franchise.")
    @ApiResponse(responseCode = "200", description = "Franchise name updated successfully")
    public Mono<ResponseEntity<Franchise>> updateFranchiseName(@PathVariable String franchiseId,
                                                                @Valid @RequestBody UpdateNameRequest request) {
        return franchiseUseCase.updateFranchiseName(franchiseId, request)
                .map(ResponseEntity::ok);
    }

    // 8. Update branch name
    @PatchMapping("/{franchiseId}/branches/{branchId}/name")
    @Operation(summary = "Update branch name", description = "Updates the name of a specific branch.")
    @ApiResponse(responseCode = "200", description = "Branch name updated successfully")
    public Mono<ResponseEntity<Franchise>> updateBranchName(@PathVariable String franchiseId,
                                                             @PathVariable String branchId,
                                                             @Valid @RequestBody UpdateNameRequest request) {
        return franchiseUseCase.updateBranchName(franchiseId, branchId, request)
                .map(ResponseEntity::ok);
    }

    // 9. Update product name
    @PatchMapping("/{franchiseId}/branches/{branchId}/products/{productId}/name")
    @Operation(summary = "Update product name", description = "Updates the name of a specific product.")
    @ApiResponse(responseCode = "200", description = "Product name updated successfully")
    public Mono<ResponseEntity<Franchise>> updateProductName(@PathVariable String franchiseId,
                                                              @PathVariable String branchId,
                                                              @PathVariable String productId,
                                                              @Valid @RequestBody UpdateNameRequest request) {
        return franchiseUseCase.updateProductName(franchiseId, branchId, productId, request)
                .map(ResponseEntity::ok);
    }
}
