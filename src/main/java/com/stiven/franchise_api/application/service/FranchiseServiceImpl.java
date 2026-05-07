package com.stiven.franchise_api.application.service;

import com.stiven.franchise_api.application.dto.BranchRequest;
import com.stiven.franchise_api.application.dto.FranchiseRequest;
import com.stiven.franchise_api.application.dto.ProductRequest;
import com.stiven.franchise_api.application.dto.TopStockProductResponse;
import com.stiven.franchise_api.application.dto.UpdateNameRequest;
import com.stiven.franchise_api.application.dto.UpdateStockRequest;
import com.stiven.franchise_api.domain.exception.DuplicateResourceException;
import com.stiven.franchise_api.domain.exception.ResourceNotFoundException;
import com.stiven.franchise_api.domain.model.Branch;
import com.stiven.franchise_api.domain.model.Franchise;
import com.stiven.franchise_api.domain.model.Product;
import com.stiven.franchise_api.domain.port.in.FranchiseUseCase;
import com.stiven.franchise_api.domain.port.out.FranchiseRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class FranchiseServiceImpl implements FranchiseUseCase {

    private final FranchiseRepositoryPort repositoryPort;

    @Override
    public Mono<Franchise> createFranchise(FranchiseRequest request) {
        return repositoryPort.existsByName(request.getName())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.<Franchise>error(new DuplicateResourceException(
                                "Franchise with name '" + request.getName() + "' already exists"));
                    }
                    Franchise franchise = Franchise.builder()
                            .name(request.getName())
                            .branches(new ArrayList<>())
                            .build();
                    return repositoryPort.save(franchise);
                });
    }

    @Override
    public Mono<Franchise> addBranch(String franchiseId, BranchRequest request) {
        return findFranchiseOrError(franchiseId)
                .flatMap(franchise -> {
                    Branch branch = Branch.builder()
                            .name(request.getName())
                            .products(new ArrayList<>())
                            .build();
                    franchise.getBranches().add(branch);
                    return repositoryPort.save(franchise);
                });
    }

    @Override
    public Mono<Franchise> addProduct(String franchiseId, String branchId, ProductRequest request) {
        return findFranchiseOrError(franchiseId)
                .flatMap(franchise -> {
                    Branch branch = findBranchOrError(franchise, branchId);
                    Product product = Product.builder()
                            .name(request.getName())
                            .stock(request.getStock())
                            .build();
                    branch.getProducts().add(product);
                    return repositoryPort.save(franchise);
                });
    }

    @Override
    public Mono<Franchise> removeProduct(String franchiseId, String branchId, String productId) {
        return findFranchiseOrError(franchiseId)
                .flatMap(franchise -> {
                    Branch branch = findBranchOrError(franchise, branchId);
                    boolean removed = branch.getProducts()
                            .removeIf(product -> product.getId().equals(productId));
                    if (!removed) {
                        return Mono.error(new ResourceNotFoundException(
                                "Product not found with id: " + productId));
                    }
                    return repositoryPort.save(franchise);
                });
    }

    @Override
    public Mono<Franchise> updateProductStock(String franchiseId, String branchId,
                                               String productId, UpdateStockRequest request) {
        return findFranchiseOrError(franchiseId)
                .flatMap(franchise -> {
                    Branch branch = findBranchOrError(franchise, branchId);
                    Product product = findProductOrError(branch, productId);
                    product.setStock(request.getStock());
                    return repositoryPort.save(franchise);
                });
    }

    @Override
    public Flux<TopStockProductResponse> getTopStockProducts(String franchiseId) {
        return findFranchiseOrError(franchiseId)
                .flatMapMany(franchise -> Flux.fromIterable(franchise.getBranches())
                        .filter(branch -> branch.getProducts() != null && !branch.getProducts().isEmpty())
                        .map(branch -> {
                            Product topProduct = branch.getProducts().stream()
                                    .max(Comparator.comparingInt(Product::getStock))
                                    .orElseThrow();
                            return TopStockProductResponse.builder()
                                    .productId(topProduct.getId())
                                    .productName(topProduct.getName())
                                    .stock(topProduct.getStock())
                                    .branchId(branch.getId())
                                    .branchName(branch.getName())
                                    .build();
                        }));
    }

    @Override
    public Mono<Franchise> updateFranchiseName(String franchiseId, UpdateNameRequest request) {
        return findFranchiseOrError(franchiseId)
                .flatMap(franchise -> {
                    franchise.setName(request.getName());
                    return repositoryPort.save(franchise);
                });
    }

    @Override
    public Mono<Franchise> updateBranchName(String franchiseId, String branchId,
                                             UpdateNameRequest request) {
        return findFranchiseOrError(franchiseId)
                .flatMap(franchise -> {
                    Branch branch = findBranchOrError(franchise, branchId);
                    branch.setName(request.getName());
                    return repositoryPort.save(franchise);
                });
    }

    @Override
    public Mono<Franchise> updateProductName(String franchiseId, String branchId,
                                              String productId, UpdateNameRequest request) {
        return findFranchiseOrError(franchiseId)
                .flatMap(franchise -> {
                    Branch branch = findBranchOrError(franchise, branchId);
                    Product product = findProductOrError(branch, productId);
                    product.setName(request.getName());
                    return repositoryPort.save(franchise);
                });
    }

    // ---- Helper methods ----

    private Mono<Franchise> findFranchiseOrError(String franchiseId) {
        return repositoryPort.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                        "Franchise not found with id: " + franchiseId)));
    }

    private Branch findBranchOrError(Franchise franchise, String branchId) {
        return franchise.getBranches().stream()
                .filter(b -> b.getId().equals(branchId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Branch not found with id: " + branchId));
    }

    private Product findProductOrError(Branch branch, String productId) {
        return branch.getProducts().stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + productId));
    }
}
