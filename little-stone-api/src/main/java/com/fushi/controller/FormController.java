package com.fushi.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fushi.common.enums.ResultCode;
import com.fushi.common.response.ApiResponse;
import com.fushi.dto.form.FormDefinitionRequestDTO;
import com.fushi.dto.form.FormInstanceRequestDTO;
import com.fushi.dto.form.ProcessDefinitionRequestDTO;
import com.fushi.dto.form.TaskOperateRequestDTO;
import com.fushi.entity.FormDefinition;
import com.fushi.entity.FormInstance;
import com.fushi.entity.ProcessDefinition;
import com.fushi.entity.Task;
import com.fushi.service.FormDefinitionService;
import com.fushi.service.FormInstanceService;
import com.fushi.service.ProcessDefinitionService;
import com.fushi.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/form")
public class FormController {

    private final FormDefinitionService formDefinitionService;
    private final FormInstanceService formInstanceService;
    private final ProcessDefinitionService processDefinitionService;
    private final TaskService taskService;

    @GetMapping("/definitions")
    public ApiResponse<IPage<FormDefinition>> getFormDefinitions(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<FormDefinition> page = new Page<>(pageNum, pageSize);
        return ApiResponse.success(formDefinitionService.page(page));
    }

    @GetMapping("/definitions/{id}")
    public ApiResponse<FormDefinition> getFormDefinition(@PathVariable Long id) {
        FormDefinition definition = formDefinitionService.getById(id);
        if (definition == null) {
            return ApiResponse.error(ResultCode.NOT_FOUND.getCode(), "表单定义不存在");
        }
        return ApiResponse.success(definition);
    }

    @PostMapping("/definitions")
    public ApiResponse<FormDefinition> createFormDefinition(@Valid @RequestBody FormDefinitionRequestDTO requestDTO) {
        try {
            FormDefinition definition = formDefinitionService.create(requestDTO, 1L);
            log.info("创建表单定义成功: {}", definition.getName());
            return ApiResponse.success(definition);
        } catch (Exception e) {
            log.error("创建表单定义失败", e);
            return ApiResponse.error(ResultCode.INTERNAL_ERROR.getCode(), e.getMessage());
        }
    }

    @PutMapping("/definitions/{id}")
    public ApiResponse<FormDefinition> updateFormDefinition(
            @PathVariable Long id,
            @Valid @RequestBody FormDefinitionRequestDTO requestDTO) {
        try {
            FormDefinition definition = formDefinitionService.update(id, requestDTO);
            log.info("更新表单定义成功: {}", id);
            return ApiResponse.success(definition);
        } catch (Exception e) {
            log.error("更新表单定义失败", e);
            return ApiResponse.error(ResultCode.INTERNAL_ERROR.getCode(), e.getMessage());
        }
    }

    @DeleteMapping("/definitions/{id}")
    public ApiResponse<?> deleteFormDefinition(@PathVariable Long id) {
        try {
            formDefinitionService.delete(id);
            log.info("删除表单定义成功: {}", id);
            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            log.error("删除表单定义失败", e);
            return ApiResponse.error(ResultCode.INTERNAL_ERROR.getCode(), e.getMessage());
        }
    }

    @PostMapping("/instances")
    public ApiResponse<FormInstance> createFormInstance(@Valid @RequestBody FormInstanceRequestDTO requestDTO) {
        try {
            FormInstance instance = formInstanceService.create(requestDTO, 1L);
            log.info("创建表单实例成功: {}", instance.getId());
            return ApiResponse.success(instance);
        } catch (Exception e) {
            log.error("创建表单实例失败", e);
            return ApiResponse.error(ResultCode.INTERNAL_ERROR.getCode(), e.getMessage());
        }
    }

    @PostMapping("/instances/submit")
    public ApiResponse<FormInstance> submitFormInstance(@Valid @RequestBody FormInstanceRequestDTO requestDTO) {
        try {
            FormInstance instance = formInstanceService.submit(requestDTO, 1L);
            log.info("提交表单实例成功: {}", instance.getId());
            return ApiResponse.success(instance);
        } catch (Exception e) {
            log.error("提交表单实例失败", e);
            return ApiResponse.error(ResultCode.INTERNAL_ERROR.getCode(), e.getMessage());
        }
    }

    @GetMapping("/instances/{id}")
    public ApiResponse<FormInstance> getFormInstance(@PathVariable Long id) {
        FormInstance instance = formInstanceService.getById(id);
        if (instance == null) {
            return ApiResponse.error(ResultCode.NOT_FOUND.getCode(), "表单实例不存在");
        }
        return ApiResponse.success(instance);
    }

    @GetMapping("/process/definitions")
    public ApiResponse<IPage<ProcessDefinition>> getProcessDefinitions(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<ProcessDefinition> page = new Page<>(pageNum, pageSize);
        return ApiResponse.success(processDefinitionService.page(page));
    }

    @PostMapping("/process/definitions")
    public ApiResponse<ProcessDefinition> createProcessDefinition(@Valid @RequestBody ProcessDefinitionRequestDTO requestDTO) {
        try {
            ProcessDefinition definition = processDefinitionService.create(requestDTO, 1L);
            log.info("创建流程定义成功: {}", definition.getName());
            return ApiResponse.success(definition);
        } catch (Exception e) {
            log.error("创建流程定义失败", e);
            return ApiResponse.error(ResultCode.INTERNAL_ERROR.getCode(), e.getMessage());
        }
    }

    @GetMapping("/tasks/todo")
    public ApiResponse<List<Task>> getTodoTasks(@RequestParam Long userId) {
        return ApiResponse.success(taskService.findTodoTasks(userId));
    }

    @PostMapping("/tasks/operate")
    public ApiResponse<Task> operateTask(@Valid @RequestBody TaskOperateRequestDTO requestDTO) {
        try {
            Task task = taskService.complete(requestDTO, 1L);
            log.info("处理任务成功: {}", task.getId());
            return ApiResponse.success(task);
        } catch (Exception e) {
            log.error("处理任务失败", e);
            return ApiResponse.error(ResultCode.INTERNAL_ERROR.getCode(), e.getMessage());
        }
    }

}