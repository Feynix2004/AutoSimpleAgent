package org.feynix.domain.task.repository;

import org.apache.ibatis.annotations.Mapper;
import org.feynix.domain.task.model.TaskEntity;
import org.feynix.infrastructure.repository.MyBatisPlusExtRepository;

/**
 * 任务仓储接口
 */
@Mapper
public interface TaskRepository extends MyBatisPlusExtRepository<TaskEntity> {


}