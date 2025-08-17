package org.feynix.application.task.service;

import org.feynix.application.task.assembler.TaskAssembler;
import org.feynix.application.task.dto.TaskDTO;
import org.feynix.domain.task.model.TaskAggregate;
import org.feynix.domain.task.model.TaskEntity;
import org.feynix.domain.task.service.TaskDomainService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务应用服务
 */
@Service
public class TaskAppService {

    private final TaskDomainService taskDomainService;


    public TaskAppService(TaskDomainService taskDomainService
    ) {
        this.taskDomainService = taskDomainService;
    }




    /**
     * 获取当前会话的最新任务
     *
     * @param sessionId 会话ID
     * @return 任务DTO列表
     */
    public TaskAggregate getCurrentSessionTask(String sessionId, String userId) {
        return taskDomainService.getCurrentSessionTask(sessionId, userId);

    }
}