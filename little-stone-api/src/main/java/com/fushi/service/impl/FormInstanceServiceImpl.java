package com.fushi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fushi.dto.form.FormInstanceRequestDTO;
import com.fushi.entity.FormDefinition;
import com.fushi.entity.FormInstance;
import com.fushi.entity.ProcessDefinition;
import com.fushi.entity.ProcessInstance;
import com.fushi.mapper.FormInstanceMapper;
import com.fushi.service.FormDefinitionService;
import com.fushi.service.FormInstanceService;
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
public class FormInstanceServiceImpl extends ServiceImpl<FormInstanceMapper, FormInstance> implements FormInstanceService {

    private final ObjectMapper objectMapper;
    private final FormDefinitionService formDefinitionService;
    private final ProcessDefinitionService processDefinitionService;
    private final ProcessInstanceService processInstanceService;
    private final TaskService taskService;

    @Override
    public FormInstance create(FormInstanceRequestDTO requestDTO, Long submitterId) {
        FormInstance instance = new FormInstance();
        instance.setFormDefinitionId(requestDTO.getFormDefinitionId());
        try {
            instance.setFormData(objectMapper.writeValueAsString(requestDTO.getFormData()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("表单数据序列化失败", e);
        }
        instance.setBusinessKey(requestDTO.getBusinessKey());
        instance.setStatus(0);
        instance.setSubmitterId(submitterId);
        instance.setCreateTime(LocalDateTime.now());
        instance.setUpdateTime(LocalDateTime.now());
        save(instance);
        return instance;
    }

    @Override
    @Transactional
    public FormInstance submit(FormInstanceRequestDTO requestDTO, Long submitterId) {
        FormDefinition definition = formDefinitionService.getById(requestDTO.getFormDefinitionId());
        if (definition == null) {
            throw new RuntimeException("表单定义不存在");
        }

        FormInstance instance = create(requestDTO, submitterId);
        instance.setStatus(1);
        instance.setSubmitTime(LocalDateTime.now());
        updateById(instance);

        ProcessDefinition processDef = processDefinitionService.getOne(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ProcessDefinition>()
                .eq("form_definition_id", requestDTO.getFormDefinitionId())
        );

        if (processDef != null) {
            ProcessInstance processInstance = processInstanceService.start(
                processDef.getId(),
                instance.getBusinessKey(),
                submitterId
            );
            instance.setProcessInstanceId(processInstance.getId());
            updateById(instance);

            try {
                List<Map<String, Object>> flowNodes = objectMapper.readValue(
                    processDef.getFlowConfig(),
                    new TypeReference<List<Map<String, Object>>>() {}
                );

                for (Map<String, Object> node : flowNodes) {
                    Boolean isStart = (Boolean) node.get("isStart");
                    if (isStart != null && isStart) {
                        String nodeId = (String) node.get("nodeId");
                        String nodeName = (String) node.get("nodeName");
                        String nodeType = (String) node.get("nodeType");
                        Long assigneeId = node.get("assigneeId") != null ? ((Number) node.get("assigneeId")).longValue() : null;

                        taskService.createTasksForNode(processInstance.getId(), nodeName, assigneeId);
                        break;
                    }
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException("流程配置解析失败", e);
            }
        }

        return instance;
    }

    @Override
    public FormInstance getById(Long id) {
        return super.getById(id);
    }
}