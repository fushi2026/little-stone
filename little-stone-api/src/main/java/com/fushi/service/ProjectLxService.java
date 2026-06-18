package com.fushi.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fushi.dto.project.ProjectLxRequestDTO;
import com.fushi.dto.project.ProjectLxResponseDTO;
import com.fushi.entity.ProjectLx;

/**
 * 立项管理服务接口
 */
public interface ProjectLxService extends IService<ProjectLx> {
    
    /**
     * 创建项目（草稿）
     *
     * @param requestDTO 项目信息
     * @param creatorId 创建人ID
     * @return 项目信息
     */
    ProjectLx create(ProjectLxRequestDTO requestDTO, Long creatorId);
    
    /**
     * 更新项目
     *
     * @param id 项目ID
     * @param requestDTO 项目信息
     * @return 项目信息
     */
    ProjectLx update(Long id, ProjectLxRequestDTO requestDTO);
    
    /**
     * 提交审批
     *
     * @param id 项目ID
     * @return 项目信息
     */
    ProjectLx submitForApproval(Long id);
    
    /**
     * 审批项目
     *
     * @param id 项目ID
     * @param approved 是否通过
     * @param comment 审批意见
     * @param approverId 审批人ID
     * @return 项目信息
     */
    ProjectLx approve(Long id, Boolean approved, String comment, Long approverId);
    
    /**
     * 取消项目
     *
     * @param id 项目ID
     * @return 项目信息
     */
    ProjectLx cancel(Long id);
    
    /**
     * 删除项目（逻辑删除）
     *
     * @param id 项目ID
     */
    void delete(Long id);
    
    /**
     * 分页查询项目列表
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param status 状态（可选）
     * @param keyword 关键词（可选）
     * @return 分页结果
     */
    IPage<ProjectLxResponseDTO> pageProjects(Integer pageNum, Integer pageSize, Integer status, String keyword);
    
    /**
     * 获取项目详情
     *
     * @param id 项目ID
     * @return 项目详情
     */
    ProjectLxResponseDTO getProjectDetail(Long id);
}
