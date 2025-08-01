package org.feynix.domain.agent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.feynix.domain.agent.model.*;
import org.feynix.domain.agent.repository.AgentRepository;
import org.feynix.domain.agent.repository.AgentVersionRepository;
import org.feynix.infrastructure.exception.BusinessException;
import org.feynix.domain.common.util.ValidationUtils;
import org.feynix.interfaces.dto.agent.SearchAgentsRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AgentDomainService{

    private final AgentRepository agentRepository;
    private final AgentVersionRepository agentVersionRepository;

    public AgentDomainService(AgentRepository agentRepository, AgentVersionRepository agentVersionRepository) {
        this.agentRepository = agentRepository;
        this.agentVersionRepository = agentVersionRepository;
    }

    /**
     * 创建新Agent
     */
    
    @Transactional
    public AgentDTO createAgent(AgentEntity agent) {
        // 参数校验
        ValidationUtils.notNull(agent, "agent");
        ValidationUtils.notEmpty(agent.getName(), "name");
        ValidationUtils.notEmpty(agent.getUserId(), "userId");

        // 保存到数据库
        agentRepository.insert(agent);
        return agent.toDTO();
    }

    /**
     * 获取单个Agent信息
     */
    
    public AgentDTO getAgent(String agentId, String userId) {
        // 参数校验
        ValidationUtils.notEmpty(agentId, "agentId");
        ValidationUtils.notEmpty(userId, "userId");

        // 需要根据 agentId 和 userId 作为条件进行查询
        LambdaQueryWrapper<AgentEntity> wrapper = Wrappers.<AgentEntity>lambdaQuery()
                .eq(AgentEntity::getId, agentId)
                .eq(AgentEntity::getUserId, userId);
        AgentEntity agent = agentRepository.selectOne(wrapper);
        if (agent == null) {
            throw new BusinessException("Agent不存在: " + agentId);
        }
        return agent.toDTO();
    }

    
    public List<AgentDTO> getUserAgents(String userId, SearchAgentsRequest searchAgentsRequest) {
        // 参数校验
        ValidationUtils.notEmpty(userId, "userId");

        // 创建基础查询条件
        LambdaQueryWrapper<AgentEntity> queryWrapper = Wrappers.<AgentEntity>lambdaQuery()
                .eq(AgentEntity::getUserId, userId)
                .like(!StringUtils.isEmpty(searchAgentsRequest.getName()), AgentEntity::getName,
                        searchAgentsRequest.getName())
                .orderByDesc(AgentEntity::getUpdatedAt);

        // 执行查询并返回结果
        List<AgentEntity> agents = agentRepository.selectList(queryWrapper);
        return agents.stream().map(AgentEntity::toDTO).collect(Collectors.toList());
    }


    /**
     * 获取已上架的Agent列表，支持名称搜索
     * 当name为空时返回所有已上架Agent
     */
    
    public List<AgentVersionDTO> getPublishedAgentsByName(SearchAgentsRequest searchAgentsRequest) {
        // 使用带名称和状态条件的查询
        List<AgentVersionEntity> latestVersions = agentVersionRepository.selectLatestVersionsByNameAndStatus(
                searchAgentsRequest.getName(),
                PublishStatus.PUBLISHED.getCode());

        // 组合助理和版本信息
        return combineAgentsWithVersions(latestVersions);
    }

    /**
     * 更新Agent信息（基本信息和配置合并更新）
     */
    
    @Transactional
    public AgentDTO updateAgent(String agentId, AgentEntity updateEntity) {
        // 参数校验
        ValidationUtils.notEmpty(agentId, "agentId");
        ValidationUtils.notNull(updateEntity, "updateEntity");
        ValidationUtils.notEmpty(updateEntity.getName(), "name");
        ValidationUtils.notEmpty(updateEntity.getUserId(), "userId");

        // 需要根据 agentId 和 userId 作为条件进行修改
        LambdaUpdateWrapper<AgentEntity> wrapper = Wrappers.<AgentEntity>lambdaUpdate()
                .eq(AgentEntity::getId, agentId)
                .eq(AgentEntity::getUserId, updateEntity.getUserId());
        agentRepository.update(updateEntity, wrapper);
        return updateEntity.toDTO();
    }

    
    public AgentDTO toggleAgentStatus(String agentId) {
        // 参数校验
        ValidationUtils.notEmpty(agentId, "agentId");

        AgentEntity agent = agentRepository.selectById(agentId);
        if (agent == null) {
            throw new BusinessException("Agent不存在: " + agentId);
        }

        // 根据当前状态切换
        if (Boolean.TRUE.equals(agent.getEnabled())) {
            agent.disable();
        } else {
            agent.enable();
        }

        agentRepository.updateById(agent);
        return agent.toDTO();
    }


    /**
     * 删除Agent
     */
    
    @Transactional
    public void deleteAgent(String agentId, String userId) {
        // 参数校验
        ValidationUtils.notEmpty(agentId, "agentId");
        ValidationUtils.notEmpty(userId, "userId");

        // 根据agentId和userId删除即可，创建 wrapper
        LambdaQueryWrapper<AgentEntity> wrapper = Wrappers.<AgentEntity>lambdaQuery()
                .eq(AgentEntity::getId, agentId)
                .eq(AgentEntity::getUserId, userId);
        agentRepository.delete(wrapper);
        // 删除版本
        agentVersionRepository.delete(Wrappers.<AgentVersionEntity>lambdaQuery()
                .eq(AgentVersionEntity::getAgentId, agentId)
                .eq(AgentVersionEntity::getUserId, userId));
    }

    /**
     * 获取Agent的最新版本
     */
    
    public AgentVersionDTO getLatestAgentVersion(String agentId) {
        // 参数校验
        ValidationUtils.notEmpty(agentId, "agentId");

        LambdaQueryWrapper<AgentVersionEntity> queryWrapper = Wrappers.<AgentVersionEntity>lambdaQuery()
                .eq(AgentVersionEntity::getAgentId, agentId)
                .orderByDesc(AgentVersionEntity::getPublishedAt)
                .last("LIMIT 1");

        AgentVersionEntity version = agentVersionRepository.selectOne(queryWrapper);
        if (version == null) {
            return null; // 第一次发布时没有版本，返回null而不是抛出异常
        }

        return version.toDTO();
    }

    
    public AgentVersionDTO publishAgentVersion(String agentId, AgentVersionEntity versionEntity) {
        // 参数校验
        ValidationUtils.notEmpty(agentId, "agentId");
        ValidationUtils.notNull(versionEntity, "versionEntity");
        ValidationUtils.notEmpty(versionEntity.getVersionNumber(), "versionNumber");
        ValidationUtils.notEmpty(versionEntity.getUserId(), "userId");
        AgentEntity agent = agentRepository.selectById(agentId);
        if (agent == null) {
            throw new BusinessException("Agent不存在: " + agentId);
        }

        // 查询最新版本号进行比较
        LambdaQueryWrapper<AgentVersionEntity> latestVersionQuery = Wrappers.<AgentVersionEntity>lambdaQuery()
                .eq(AgentVersionEntity::getAgentId, agentId)
                .eq(AgentVersionEntity::getUserId, versionEntity.getUserId())
                .orderByDesc(AgentVersionEntity::getPublishedAt)
                .last("LIMIT 1");

        AgentVersionEntity latestVersion = agentVersionRepository.selectOne(latestVersionQuery);

        if (latestVersion != null) {
            // 版本号比较
            String newVersion = versionEntity.getVersionNumber();
            String oldVersion = latestVersion.getVersionNumber();

            // 检查是否为相同版本号
            if (newVersion.equals(oldVersion)) {
                throw new BusinessException("版本号已存在: " + newVersion);
            }

            // 检查新版本号是否大于旧版本号
            if (!isVersionGreaterThan(newVersion, oldVersion)) {
                throw new BusinessException("新版本号(" + newVersion + ")必须大于当前最新版本号(" + oldVersion + ")");
            }
        }

        // 设置版本关联的Agent ID
        versionEntity.setAgentId(agentId);

        // 设置版本状态为审核中
        versionEntity.setPublishStatus(PublishStatus.REVIEWING.getCode());

        // 保存版本
        agentVersionRepository.insert(versionEntity);

        return versionEntity.toDTO();
    }

    
    public List<AgentVersionDTO> getAgentVersions(String agentId, String userId) {
        // 参数校验
        ValidationUtils.notEmpty(agentId, "agentId");
        ValidationUtils.notEmpty(userId, "userId");
        LambdaQueryWrapper<AgentVersionEntity> queryWrapper = Wrappers.<AgentVersionEntity>lambdaQuery()
                .eq(AgentVersionEntity::getAgentId, agentId)
                .eq(AgentVersionEntity::getUserId, userId)
                .orderByDesc(AgentVersionEntity::getPublishedAt);

        List<AgentVersionEntity> versions = agentVersionRepository.selectList(queryWrapper);
        return versions.stream().map(AgentVersionEntity::toDTO).collect(Collectors.toList());
    }

    
    public AgentVersionDTO getAgentVersion(String agentId, String versionNumber) {
        // 参数校验
        ValidationUtils.notEmpty(agentId, "agentId");
        ValidationUtils.notEmpty(versionNumber, "versionNumber");

        LambdaQueryWrapper<AgentVersionEntity> queryWrapper = Wrappers.<AgentVersionEntity>lambdaQuery()
                .eq(AgentVersionEntity::getAgentId, agentId)
                .eq(AgentVersionEntity::getVersionNumber, versionNumber);

        AgentVersionEntity version = agentVersionRepository.selectOne(queryWrapper);
        if (version == null) {
            throw new BusinessException("版本不存在: " + versionNumber);
        }

        return version.toDTO();
    }

    
    public List<AgentVersionDTO> getVersionsByStatus(PublishStatus status) {

        // 直接通过SQL查询每个agentId的最新版本
        List<AgentVersionEntity> latestVersions = agentVersionRepository
                .selectLatestVersionsByStatus(status == null ? null : status.getCode());

        // 转换为DTO列表并返回
        return latestVersions.stream()
                .map(AgentVersionEntity::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 拒绝版本发布
     */
    
    @Transactional
    public AgentVersionDTO rejectVersion(String versionId, String reason) {
        // 参数校验
        ValidationUtils.notEmpty(versionId, "versionId");
        ValidationUtils.notEmpty(reason, "reason");

        AgentVersionEntity version = agentVersionRepository.selectById(versionId);
        if (version == null) {
            throw new BusinessException("版本不存在: " + versionId);
        }

        // 拒绝版本发布
        version.reject(reason);
        agentVersionRepository.updateById(version);

        return version.toDTO();
    }

    /**
     * 更新版本发布状态
     */
    
    @Transactional
    public AgentVersionDTO updateVersionPublishStatus(String versionId, PublishStatus status) {
        // 参数校验
        ValidationUtils.notEmpty(versionId, "versionId");
        ValidationUtils.notNull(status, "status");

        AgentVersionEntity version = agentVersionRepository.selectById(versionId);
        if (version == null) {
            throw new BusinessException("版本不存在: " + versionId);
        }

        version.setRejectReason("");

        // 更新版本状态
        version.updatePublishStatus(status);
        agentVersionRepository.updateById(version);

        // 如果状态更新为已发布，则绑定为Agent的publishedVersion
        if (status == PublishStatus.PUBLISHED) {
            AgentEntity agent = agentRepository.selectById(version.getAgentId());
            if (agent != null) {
                agent.publishVersion(versionId);
                agentRepository.updateById(agent);
            }
        }

        return version.toDTO();
    }

    /**
     * 比较版本号大小
     *
     * @param newVersion 新版本号
     * @param oldVersion 旧版本号
     * @return 如果新版本大于旧版本返回true，否则返回false
     */
    private boolean isVersionGreaterThan(String newVersion, String oldVersion) {
        if (oldVersion == null || oldVersion.trim().isEmpty()) {
            return true; // 如果没有旧版本，新版本肯定更大
        }

        // 分割版本号
        String[] current = newVersion.split("\\.");
        String[] last = oldVersion.split("\\.");

        // 确保版本号格式正确
        if (current.length != 3 || last.length != 3) {
            throw new BusinessException("版本号必须遵循 x.y.z 格式");
        }

        try {
            // 比较主版本号
            int currentMajor = Integer.parseInt(current[0]);
            int lastMajor = Integer.parseInt(last[0]);
            if (currentMajor > lastMajor)
                return true;
            if (currentMajor < lastMajor)
                return false;

            // 主版本号相同，比较次版本号
            int currentMinor = Integer.parseInt(current[1]);
            int lastMinor = Integer.parseInt(last[1]);
            if (currentMinor > lastMinor)
                return true;
            if (currentMinor < lastMinor)
                return false;

            // 主版本号和次版本号都相同，比较修订版本号
            int currentPatch = Integer.parseInt(current[2]);
            int lastPatch = Integer.parseInt(last[2]);

            return currentPatch > lastPatch;
        } catch (NumberFormatException e) {
            throw new BusinessException("版本号格式错误，必须是数字: " + e.getMessage());
        }
    }

    /**
     * 组合助理和版本信息
     *
     * @param versionEntities 版本实体列表
     * @return 组合后的版本DTO列表
     */
    private List<AgentVersionDTO> combineAgentsWithVersions(List<AgentVersionEntity> versionEntities) {
        // 如果版本列表为空，直接返回空列表
        if (versionEntities == null || versionEntities.isEmpty()) {
            return Collections.emptyList();
        }

        // 根据版本中的 agent_id 以及 enable == true 查出对应的 agents
        List<AgentEntity> agents = agentRepository.selectList(Wrappers.<AgentEntity>lambdaQuery()
                .in(AgentEntity::getId,
                        versionEntities.stream().map(AgentVersionEntity::getAgentId).collect(Collectors.toList()))
                .eq(AgentEntity::getEnabled, true));

        // 将版本转为 map，key：agent_id，value：本身
        Map<String, AgentVersionEntity> agentVersionMap = versionEntities.stream()
                .collect(Collectors.toMap(AgentVersionEntity::getAgentId, Function.identity()));

        // 遍历 agents，从 map 中找到组装为 AgentVersionDTO 返回
        return agents.stream()
                .map(agent -> {
                    AgentVersionEntity version = agentVersionMap.get(agent.getId());
                    return version != null ? version.toDTO() : null;
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }
}
