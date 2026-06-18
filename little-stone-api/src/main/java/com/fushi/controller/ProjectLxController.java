package com.fushi.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fushi.common.enums.ResultCode;
import com.fushi.common.response.ApiResponse;
import com.fushi.dto.project.ProjectLxRequestDTO;
import com.fushi.dto.project.ProjectLxResponseDTO;
import com.fushi.entity.ProjectLx;
import com.fushi.service.ProjectLxService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 立项管理控制器
 */
@Slf4j
@Tag(name = "立项管理", description = "项目立项相关接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project/lx")
public class ProjectLxController {

    private final ProjectLxService projectLxService;

    @Operation(summary = "创建项目（草稿）", description = "创建一个新的项目立项申请，默认为草稿状态")
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('project:lx:create')")
    public ApiResponse<ProjectLx> create(@Valid @RequestBody ProjectLxRequestDTO requestDTO) {
        try {
            // TODO: 从当前登录用户获取 creatorId
            Long creatorId = 1L;
            ProjectLx project = projectLxService.create(requestDTO, creatorId);
            log.info("创建项目成功: {}", project.getId());
            return ApiResponse.success(project);
        } catch (Exception e) {
            log.error("创建项目失败", e);
            return ApiResponse.error(ResultCode.INTERNAL_ERROR.getCode(), e.getMessage());
        }
    }

    @Operation(summary = "更新项目", description = "更新项目信息，仅草稿和已驳回状态可编辑")
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('project:lx:update')")
    public ApiResponse<ProjectLx> update(@PathVariable Long id, 
                                         @Valid @RequestBody ProjectLxRequestDTO requestDTO) {
        try {
            ProjectLx project = projectLxService.update(id, requestDTO);
            log.info("更新项目成功: {}", id);
            return ApiResponse.success(project);
        } catch (Exception e) {
            log.error("更新项目失败", e);
            return ApiResponse.error(ResultCode.INTERNAL_ERROR.getCode(), e.getMessage());
        }
    }

    @Operation(summary = "提交审批", description = "将项目提交审批")
    @PostMapping("/submit/{id}")
    @PreAuthorize("hasAuthority('project:lx:submit')")
    public ApiResponse<ProjectLx> submitForApproval(@PathVariable Long id) {
        try {
            ProjectLx project = projectLxService.submitForApproval(id);
            log.info("项目提交审批成功: {}", id);
            return ApiResponse.success(project);
        } catch (Exception e) {
            log.error("提交审批失败", e);
            return ApiResponse.error(ResultCode.INTERNAL_ERROR.getCode(), e.getMessage());
        }
    }

    @Operation(summary = "审批项目", description = "对项目进行审批（通过或驳回）")
    @PostMapping("/approve/{id}")
    @PreAuthorize("hasAuthority('project:lx:approve')")
    public ApiResponse<ProjectLx> approve(@PathVariable Long id,
                                          @RequestParam Boolean approved,
                                          @RequestParam(required = false) String comment) {
        try {
            // TODO: 从当前登录用户获取 approverId
            Long approverId = 1L;
            ProjectLx project = projectLxService.approve(id, approved, comment, approverId);
            log.info("项目审批完成: {}, 结果: {}", id, approved ? "通过" : "驳回");
            return ApiResponse.success(project);
        } catch (Exception e) {
            log.error("审批项目失败", e);
            return ApiResponse.error(ResultCode.INTERNAL_ERROR.getCode(), e.getMessage());
        }
    }

    @Operation(summary = "取消项目", description = "取消项目立项")
    @PostMapping("/cancel/{id}")
    @PreAuthorize("hasAuthority('project:lx:cancel')")
    public ApiResponse<ProjectLx> cancel(@PathVariable Long id) {
        try {
            ProjectLx project = projectLxService.cancel(id);
            log.info("项目已取消: {}", id);
            return ApiResponse.success(project);
        } catch (Exception e) {
            log.error("取消项目失败", e);
            return ApiResponse.error(ResultCode.INTERNAL_ERROR.getCode(), e.getMessage());
        }
    }

    @Operation(summary = "删除项目", description = "逻辑删除项目")
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('project:lx:delete')")
    public ApiResponse<?> delete(@PathVariable Long id) {
        try {
            projectLxService.delete(id);
            log.info("项目已删除: {}", id);
            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            log.error("删除项目失败", e);
            return ApiResponse.error(ResultCode.INTERNAL_ERROR.getCode(), e.getMessage());
        }
    }

    @Operation(summary = "获取项目详情", description = "根据ID获取项目详细信息")
    @GetMapping("/detail/{id}")
    @PreAuthorize("hasAuthority('project:lx:query')")
    public ApiResponse<ProjectLxResponseDTO> getDetail(@PathVariable Long id) {
        try {
            ProjectLxResponseDTO project = projectLxService.getProjectDetail(id);
            return ApiResponse.success(project);
        } catch (Exception e) {
            log.error("获取项目详情失败", e);
            return ApiResponse.error(ResultCode.INTERNAL_ERROR.getCode(), e.getMessage());
        }
    }

    @Operation(summary = "分页查询项目列表", description = "支持按状态和关键词搜索")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('project:lx:query')")
    public ApiResponse<IPage<ProjectLxResponseDTO>> pageList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "状态筛选") @RequestParam(required = false) Integer status,
            @Parameter(description = "关键词搜索") @RequestParam(required = false) String keyword) {
        try {
            IPage<ProjectLxResponseDTO> page = projectLxService.pageProjects(pageNum, pageSize, status, keyword);
            return ApiResponse.success(page);
        } catch (Exception e) {
            log.error("查询项目列表失败", e);
            return ApiResponse.error(ResultCode.INTERNAL_ERROR.getCode(), e.getMessage());
        }
    }
}

