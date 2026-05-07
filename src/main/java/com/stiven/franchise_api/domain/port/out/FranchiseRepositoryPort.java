package com.stiven.franchise_api.domain.port.out;

import com.stiven.franchise_api.domain.model.Franchise;
import reactor.core.publisher.Mono;

public interface FranchiseRepositoryPort {

    Mono<Franchise> save(Franchise franchise);

    Mono<Franchise> findById(String id);

    Mono<Boolean> existsByName(String name);
}
