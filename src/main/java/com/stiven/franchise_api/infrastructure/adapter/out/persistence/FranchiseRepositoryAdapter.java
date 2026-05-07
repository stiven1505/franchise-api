package com.stiven.franchise_api.infrastructure.adapter.out.persistence;

import com.stiven.franchise_api.domain.model.Franchise;
import com.stiven.franchise_api.domain.port.out.FranchiseRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FranchiseRepositoryAdapter implements FranchiseRepositoryPort {

    private final MongoFranchiseRepository mongoRepository;

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        return mongoRepository.save(franchise);
    }

    @Override
    public Mono<Franchise> findById(String id) {
        return mongoRepository.findById(id);
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return mongoRepository.existsByName(name);
    }
}
