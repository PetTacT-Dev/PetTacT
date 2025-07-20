package com.pettact.api.mongo;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

@Configuration
@RequiredArgsConstructor
public class MongoTTLConfig {

    private final MongoTemplate mongoTemplate;

    @PostConstruct
    public void initTTLIndex() {
        Index index = new Index()
                .on("timestamp", Sort.Direction.ASC)
                .expire(604800); // 1일후 로그 데이터 삭제 (초 단위로 기록됨) 
        mongoTemplate.indexOps("httplogs").ensureIndex(index);
    }
}
