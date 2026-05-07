package com.stiven.franchise_api.infrastructure.adapter.out.persistence;

import com.stiven.franchise_api.domain.model.Franchise;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface MongoFranchiseRepository extends ReactiveMongoRepository<Franchise, String> {

    Mono<Boolean> existsByName(String name);
}
