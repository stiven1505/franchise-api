package com.stiven.franchise_api.service;

import com.stiven.franchise_api.application.dto.BranchRequest;
import com.stiven.franchise_api.application.dto.FranchiseRequest;
import com.stiven.franchise_api.application.dto.ProductRequest;
import com.stiven.franchise_api.application.dto.TopStockProductResponse;
import com.stiven.franchise_api.application.dto.UpdateNameRequest;
import com.stiven.franchise_api.application.dto.UpdateStockRequest;
import com.stiven.franchise_api.application.service.FranchiseServiceImpl;
import com.stiven.franchise_api.domain.exception.DuplicateResourceException;
import com.stiven.franchise_api.domain.exception.ResourceNotFoundException;
import com.stiven.franchise_api.domain.model.Branch;
import com.stiven.franchise_api.domain.model.Franchise;
import com.stiven.franchise_api.domain.model.Product;
import com.stiven.franchise_api.domain.port.out.FranchiseRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseServiceImplTest {

    @Mock
    private FranchiseRepositoryPort repositoryPort;

    @InjectMocks
    private FranchiseServiceImpl service;

    private Franchise testFranchise;
    private Branch testBranch;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id("product-1")
                .name("Big Mac")
                .stock(100)
                .build();

        testBranch = Branch.builder()
                .id("branch-1")
                .name("Sucursal Norte")
                .products(new ArrayList<>(List.of(testProduct)))
                .build();

        testFranchise = Franchise.builder()
                .id("franchise-1")
                .name("McDonald's")
                .branches(new ArrayList<>(List.of(testBranch)))
                .build();
    }

    // ---- createFranchise ----

    @Test
    void createFranchise_success() {
        when(repositoryPort.existsByName("New Franchise")).thenReturn(Mono.just(false));
        when(repositoryPort.save(any(Franchise.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(service.createFranchise(new FranchiseRequest("New Franchise")))
                .assertNext(franchise -> {
                    assertThat(franchise.getName()).isEqualTo("New Franchise");
                    assertThat(franchise.getBranches()).isEmpty();
                })
                .verifyComplete();
    }

    @Test
    void createFranchise_duplicateName() {
        when(repositoryPort.existsByName("McDonald's")).thenReturn(Mono.just(true));

        StepVerifier.create(service.createFranchise(new FranchiseRequest("McDonald's")))
                .expectError(DuplicateResourceException.class)
                .verify();
    }

    // ---- addBranch ----

    @Test
    void addBranch_success() {
        when(repositoryPort.findById("franchise-1")).thenReturn(Mono.just(testFranchise));
        when(repositoryPort.save(any(Franchise.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(service.addBranch("franchise-1", new BranchRequest("Sucursal Sur")))
                .assertNext(franchise -> {
                    assertThat(franchise.getBranches()).hasSize(2);
                    assertThat(franchise.getBranches().get(1).getName()).isEqualTo("Sucursal Sur");
                    assertThat(franchise.getBranches().get(1).getProducts()).isEmpty();
                })
                .verifyComplete();
    }

    @Test
    void addBranch_franchiseNotFound() {
        when(repositoryPort.findById("invalid-id")).thenReturn(Mono.empty());

        StepVerifier.create(service.addBranch("invalid-id", new BranchRequest("Sucursal")))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    // ---- addProduct ----

    @Test
    void addProduct_success() {
        when(repositoryPort.findById("franchise-1")).thenReturn(Mono.just(testFranchise));
        when(repositoryPort.save(any(Franchise.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(service.addProduct("franchise-1", "branch-1", new ProductRequest("McFlurry", 50)))
                .assertNext(franchise -> {
                    Branch branch = franchise.getBranches().get(0);
                    assertThat(branch.getProducts()).hasSize(2);
                    assertThat(branch.getProducts().get(1).getName()).isEqualTo("McFlurry");
                    assertThat(branch.getProducts().get(1).getStock()).isEqualTo(50);
                })
                .verifyComplete();
    }

    @Test
    void addProduct_branchNotFound() {
        when(repositoryPort.findById("franchise-1")).thenReturn(Mono.just(testFranchise));

        StepVerifier.create(service.addProduct("franchise-1", "invalid-branch", new ProductRequest("McFlurry", 50)))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    // ---- removeProduct ----

    @Test
    void removeProduct_success() {
        when(repositoryPort.findById("franchise-1")).thenReturn(Mono.just(testFranchise));
        when(repositoryPort.save(any(Franchise.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(service.removeProduct("franchise-1", "branch-1", "product-1"))
                .assertNext(franchise -> {
                    assertThat(franchise.getBranches().get(0).getProducts()).isEmpty();
                })
                .verifyComplete();
    }

    @Test
    void removeProduct_productNotFound() {
        when(repositoryPort.findById("franchise-1")).thenReturn(Mono.just(testFranchise));

        StepVerifier.create(service.removeProduct("franchise-1", "branch-1", "invalid-product"))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    // ---- updateProductStock ----

    @Test
    void updateProductStock_success() {
        when(repositoryPort.findById("franchise-1")).thenReturn(Mono.just(testFranchise));
        when(repositoryPort.save(any(Franchise.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(service.updateProductStock("franchise-1", "branch-1", "product-1", new UpdateStockRequest(250)))
                .assertNext(franchise -> {
                    Product product = franchise.getBranches().get(0).getProducts().get(0);
                    assertThat(product.getStock()).isEqualTo(250);
                })
                .verifyComplete();
    }

    @Test
    void updateProductStock_productNotFound() {
        when(repositoryPort.findById("franchise-1")).thenReturn(Mono.just(testFranchise));

        StepVerifier.create(service.updateProductStock("franchise-1", "branch-1", "invalid-product", new UpdateStockRequest(250)))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    // ---- getTopStockProducts ----

    @Test
    void getTopStockProducts_success() {
        Product product2 = Product.builder().id("product-2").name("McFlurry").stock(200).build();
        testBranch.getProducts().add(product2);

        Branch branch2 = Branch.builder()
                .id("branch-2")
                .name("Sucursal Sur")
                .products(new ArrayList<>(List.of(
                        Product.builder().id("product-3").name("Nuggets").stock(300).build(),
                        Product.builder().id("product-4").name("Fries").stock(50).build()
                )))
                .build();
        testFranchise.getBranches().add(branch2);

        when(repositoryPort.findById("franchise-1")).thenReturn(Mono.just(testFranchise));

        StepVerifier.create(service.getTopStockProducts("franchise-1"))
                .assertNext(response -> {
                    // Branch 1: McFlurry (200) > Big Mac (100)
                    assertThat(response.getProductName()).isEqualTo("McFlurry");
                    assertThat(response.getStock()).isEqualTo(200);
                    assertThat(response.getBranchName()).isEqualTo("Sucursal Norte");
                })
                .assertNext(response -> {
                    // Branch 2: Nuggets (300) > Fries (50)
                    assertThat(response.getProductName()).isEqualTo("Nuggets");
                    assertThat(response.getStock()).isEqualTo(300);
                    assertThat(response.getBranchName()).isEqualTo("Sucursal Sur");
                })
                .verifyComplete();
    }

    @Test
    void getTopStockProducts_emptyBranches() {
        Franchise emptyFranchise = Franchise.builder()
                .id("franchise-2")
                .name("Empty Franchise")
                .branches(new ArrayList<>())
                .build();

        when(repositoryPort.findById("franchise-2")).thenReturn(Mono.just(emptyFranchise));

        StepVerifier.create(service.getTopStockProducts("franchise-2"))
                .verifyComplete();
    }

    // ---- updateFranchiseName ----

    @Test
    void updateFranchiseName_success() {
        when(repositoryPort.findById("franchise-1")).thenReturn(Mono.just(testFranchise));
        when(repositoryPort.save(any(Franchise.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(service.updateFranchiseName("franchise-1", new UpdateNameRequest("Burger King")))
                .assertNext(franchise -> {
                    assertThat(franchise.getName()).isEqualTo("Burger King");
                })
                .verifyComplete();
    }

    // ---- updateBranchName ----

    @Test
    void updateBranchName_success() {
        when(repositoryPort.findById("franchise-1")).thenReturn(Mono.just(testFranchise));
        when(repositoryPort.save(any(Franchise.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(service.updateBranchName("franchise-1", "branch-1", new UpdateNameRequest("Sucursal Centro")))
                .assertNext(franchise -> {
                    assertThat(franchise.getBranches().get(0).getName()).isEqualTo("Sucursal Centro");
                })
                .verifyComplete();
    }

    // ---- updateProductName ----

    @Test
    void updateProductName_success() {
        when(repositoryPort.findById("franchise-1")).thenReturn(Mono.just(testFranchise));
        when(repositoryPort.save(any(Franchise.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(service.updateProductName("franchise-1", "branch-1", "product-1", new UpdateNameRequest("Quarter Pounder")))
                .assertNext(franchise -> {
                    assertThat(franchise.getBranches().get(0).getProducts().get(0).getName()).isEqualTo("Quarter Pounder");
                })
                .verifyComplete();
    }
}
