package com.fushi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fushi.dto.form.TaskOperateRequestDTO;
import com.fushi.entity.ProcessDefinition;
import com.fushi.entity.ProcessInstance;
import com.fushi.entity.Task;
import com.fushi.mapper.TaskMapper;
import com.fushi.service.ProcessDefinitionService;
import com.fushi.service.ProcessInstanceService;
import com.fushi.service.TaskService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {

    private final ObjectMapper objectMapper;
    private final ProcessDefinitionService processDefinitionService;
    private final ProcessInstanceService processInstanceService;

    @Override
    public Task create(Long processInstanceId, String nodeName, String nodeType, Long assigneeId, Long creatorId) {
        Task task = new Task();
        task.setProcessInstanceId(processInstanceId);
        task.setNodeName(nodeName);
        task.setNodeType(nodeType);
        task.setAssigneeId(assigneeId);
        task.setCreatorId(creatorId);
        task.setStatus(0);
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        save(task);
        return task;
    }

    @Override
    @Transactional
    public Task complete(TaskOperateRequestDTO requestDTO, Long operatorId) {
        Task task = getById(requestDTO.getTaskId());
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        if (task.getStatus() != 0) {
            throw new RuntimeException("任务已处理");
        }

        task.setStatus("approve".equals(requestDTO.getAction()) ? 1 : 2);
        task.setAction(requestDTO.getAction());
        task.setComment(requestDTO.getComment());
        task.setCompleteTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        updateById(task);

        if ("approve".equals(requestDTO.getAction())) {
            ProcessInstance processInstance = processInstanceService.getById(task.getProcessInstanceId());
            if (processInstance != null) {
                ProcessDefinition processDef = processDefinitionService.getById(processInstance.getProcessDefinitionId());
                if (processDef != null) {
                    try {
                        List<Map<String, Object>> flowNodes = objectMapper.readValue(
                            processDef.getFlowConfig(),
                            new TypeReference<List<Map<String, Object>>>() {}
                        );

                        Map<String, Object> currentNode = null;
                        for (Map<String, Object> node : flowNodes) {
                            String nodeName = (String) node.get("nodeName");
                            if (task.getNodeName().equals(nodeName)) {
                                currentNode = node;
                                break;
                            }
                        }

                        if (currentNode != null) {
                            @SuppressWarnings("unchecked")
                            List<String> nextNodes = (List<String>) currentNode.get("nextNodes");
                            if (nextNodes != null && !nextNodes.isEmpty()) {
                                for (String nextNodeId : nextNodes) {
                                    for (Map<String, Object> node : flowNodes) {
                                        String nodeId = (String) node.get("nodeId");
                                        if (nextNodeId.equals(nodeId)) {
                                            String nextNodeName = (String) node.get("nodeName");
                                            String nextNodeType = (String) node.get("nodeType");
                                            Long nextAssigneeId = node.get("assigneeId") != null 
                                                ? ((Number) node.get("assigneeId")).longValue() 
                                                : null;

                                            create(task.getProcessInstanceId(), nextNodeName, nextNodeType, nextAssigneeId, operatorId);
                                            processInstanceService.updateCurrentNode(task.getProcessInstanceId(), nextNodeName);
                                        }
                                    }
                                }
                            } else {
                                processInstanceService.complete(processInstance.getId());
                            }
                        }
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("流程配置解析失败", e);
                    }
                }
            }
        }

        return task;
    }

    @Override
    public List<Task> findTodoTasks(Long userId) {
        return ((TaskMapper) getBaseMapper()).selectByAssigneeAndStatus(userId, 0);
    }

    @Override
    public List<Task> findByProcessInstanceId(Long processInstanceId) {
        return ((TaskMapper) getBaseMapper()).selectByProcessInstanceId(processInstanceId);
    }

    @Override
    public void createTasksForNode(Long processInstanceId, String nodeName, Long assigneeId) {
        ProcessInstance processInstance = processInstanceService.getById(processInstanceId);
        if (processInstance != null) {
            ProcessDefinition processDef = processDefinitionService.getById(processInstance.getProcessDefinitionId());
            if (processDef != null) {
                try {
                    List<Map<String, Object>> flowNodes = objectMapper.readValue(
                        processDef.getFlowConfig(),
                        new TypeReference<List<Map<String, Object>>>() {}
                    );

                    for (Map<String, Object> node : flowNodes) {
                        String name = (String) node.get("nodeName");
                        if (nodeName.equals(name)) {
                            String nodeType = (String) node.get("nodeType");
                            create(processInstanceId, nodeName, nodeType, assigneeId, processInstance.getInitiatorId());
                            processInstanceService.updateCurrentNode(processInstanceId, nodeName);
                            break;
                        }
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("流程配置解析失败", e);
                }
            }
        }
    }
}