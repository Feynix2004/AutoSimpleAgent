package org.feynix.domain.llm.repository;

import org.apache.ibatis.annotations.Mapper;
import org.feynix.domain.llm.model.ModelEntity;
import org.feynix.domain.llm.model.ProviderEntity;
import org.feynix.infrastructure.repository.MyBatisPlusExtRepository;

@Mapper
public interface ProviderRepository  extends MyBatisPlusExtRepository<ProviderEntity> {
}
