package com.stiven.franchise_api.controller;

import com.stiven.franchise_api.application.dto.BranchRequest;
import com.stiven.franchise_api.application.dto.FranchiseRequest;
import com.stiven.franchise_api.application.dto.ProductRequest;
import com.stiven.franchise_api.application.dto.TopStockProductResponse;
import com.stiven.franchise_api.application.dto.UpdateNameRequest;
import com.stiven.franchise_api.application.dto.UpdateStockRequest;
import com.stiven.franchise_api.domain.exception.ResourceNotFoundException;
import com.stiven.franchise_api.domain.model.Branch;
import com.stiven.franchise_api.domain.model.Franchise;
import com.stiven.franchise_api.domain.model.Product;
import com.stiven.franchise_api.domain.port.in.FranchiseUseCase;
import com.stiven.franchise_api.infrastructure.adapter.in.rest.FranchiseController;
import com.stiven.franchise_api.infrastructure.adapter.in.rest.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(FranchiseController.class)
@Import(GlobalExceptionHandler.class)
class FranchiseControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private FranchiseUseCase franchiseUseCase;

    private Franchise testFranchise;

    private static final String BASE_URL = "/api/v1/franchises";

    @BeforeEach
    void setUp() {
        Product product = Product.builder().id("p1").name("Big Mac").stock(100).build();
        Branch branch = Branch.builder().id("b1").name("Sucursal Norte")
                .products(new ArrayList<>(List.of(product))).build();
        testFranchise = Franchise.builder().id("f1").name("McDonald's")
                .branches(new ArrayList<>(List.of(branch))).build();
    }

    @Test
    void createFranchise_returns201() {
        when(franchiseUseCase.createFranchise(any(FranchiseRequest.class)))
                .thenReturn(Mono.just(testFranchise));

        webTestClient.post().uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new FranchiseRequest("McDonald's"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("McDonald's")
                .jsonPath("$.id").isEqualTo("f1");
    }

    @Test
    void createFranchise_invalidBody_returns400() {
        webTestClient.post().uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new FranchiseRequest(""))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void addBranch_returns200() {
        when(franchiseUseCase.addBranch(eq("f1"), any(BranchRequest.class)))
                .thenReturn(Mono.just(testFranchise));

        webTestClient.post().uri(BASE_URL + "/f1/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new BranchRequest("Sucursal Sur"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("f1");
    }

    @Test
    void addProduct_returns200() {
        when(franchiseUseCase.addProduct(eq("f1"), eq("b1"), any(ProductRequest.class)))
                .thenReturn(Mono.just(testFranchise));

        webTestClient.post().uri(BASE_URL + "/f1/branches/b1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ProductRequest("McFlurry", 50))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void removeProduct_returns200() {
        when(franchiseUseCase.removeProduct("f1", "b1", "p1"))
                .thenReturn(Mono.just(testFranchise));

        webTestClient.delete().uri(BASE_URL + "/f1/branches/b1/products/p1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void updateProductStock_returns200() {
        when(franchiseUseCase.updateProductStock(eq("f1"), eq("b1"), eq("p1"), any(UpdateStockRequest.class)))
                .thenReturn(Mono.just(testFranchise));

        webTestClient.patch().uri(BASE_URL + "/f1/branches/b1/products/p1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateStockRequest(200))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getTopStockProducts_returns200() {
        TopStockProductResponse response = TopStockProductResponse.builder()
                .productId("p1").productName("Big Mac").stock(100)
                .branchId("b1").branchName("Sucursal Norte").build();

        when(franchiseUseCase.getTopStockProducts("f1"))
                .thenReturn(Flux.just(response));

        webTestClient.get().uri(BASE_URL + "/f1/top-stock-products")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TopStockProductResponse.class).hasSize(1);
    }

    @Test
    void updateFranchiseName_returns200() {
        when(franchiseUseCase.updateFranchiseName(eq("f1"), any(UpdateNameRequest.class)))
                .thenReturn(Mono.just(testFranchise));

        webTestClient.patch().uri(BASE_URL + "/f1/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateNameRequest("Burger King"))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void updateBranchName_returns200() {
        when(franchiseUseCase.updateBranchName(eq("f1"), eq("b1"), any(UpdateNameRequest.class)))
                .thenReturn(Mono.just(testFranchise));

        webTestClient.patch().uri(BASE_URL + "/f1/branches/b1/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateNameRequest("Sucursal Centro"))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void updateProductName_returns200() {
        when(franchiseUseCase.updateProductName(eq("f1"), eq("b1"), eq("p1"), any(UpdateNameRequest.class)))
                .thenReturn(Mono.just(testFranchise));

        webTestClient.patch().uri(BASE_URL + "/f1/branches/b1/products/p1/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateNameRequest("Quarter Pounder"))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void franchiseNotFound_returns404() {
        when(franchiseUseCase.addBranch(eq("invalid"), any(BranchRequest.class)))
                .thenReturn(Mono.error(new ResourceNotFoundException("Franchise not found with id: invalid")));

        webTestClient.post().uri(BASE_URL + "/invalid/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new BranchRequest("Test"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.message").isEqualTo("Franchise not found with id: invalid");
    }
}
