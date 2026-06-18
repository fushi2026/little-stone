package com.fushi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fushi.common.enums.ResultCode;
import com.fushi.dto.project.ProjectLxRequestDTO;
import com.fushi.dto.project.ProjectLxResponseDTO;
import com.fushi.entity.ProjectLx;
import com.fushi.mapper.ProjectLxMapper;
import com.fushi.service.ProjectLxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 立项管理服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectLxServiceImpl extends ServiceImpl<ProjectLxMapper, ProjectLx> implements ProjectLxService {

    /**
     * 状态描述映射
     */
    private static final String[] STATUS_DESC = {"草稿", "待审批", "已立项", "已驳回", "已取消"};
    
    /**
     * 优先级描述映射
     */
    private static final String[] PRIORITY_DESC = {"低", "中", "高", "紧急"};

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectLx create(ProjectLxRequestDTO requestDTO, Long creatorId) {
        ProjectLx project = new ProjectLx();
        BeanUtil.copyProperties(requestDTO, project);
        
        // 生成项目编号
        String projectCode = generateProjectCode();
        project.setProjectCode(projectCode);
        
        // 设置默认值
        project.setStatus(0); // 草稿状态
        project.setCreatorId(creatorId);
        project.setDeleted(0);
        project.setCreateTime(LocalDateTime.now());
        project.setUpdateTime(LocalDateTime.now());
        
        save(project);
        log.info("创建项目成功: {}, 编号: {}", project.getProjectName(), projectCode);
        return project;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectLx update(Long id, ProjectLxRequestDTO requestDTO) {
        ProjectLx project = getById(id);
        if (project == null || project.getDeleted() == 1) {
            throw new RuntimeException("项目不存在");
        }
        
        // 只有草稿和已驳回状态可以编辑
        if (project.getStatus() != 0 && project.getStatus() != 3) {
            throw new RuntimeException("当前状态不允许编辑");
        }
        
        BeanUtil.copyProperties(requestDTO, project);
        project.setUpdateTime(LocalDateTime.now());
        
        updateById(project);
        log.info("更新项目成功: {}", id);
        return project;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectLx submitForApproval(Long id) {
        ProjectLx project = getById(id);
        if (project == null || project.getDeleted() == 1) {
            throw new RuntimeException("项目不存在");
        }
        
        if (project.getStatus() != 0 && project.getStatus() != 3) {
            throw new RuntimeException("当前状态不允许提交审批");
        }
        
        project.setStatus(1); // 待审批
        project.setUpdateTime(LocalDateTime.now());
        
        updateById(project);
        log.info("项目提交审批成功: {}", id);
        return project;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectLx approve(Long id, Boolean approved, String comment, Long approverId) {
        ProjectLx project = getById(id);
        if (project == null || project.getDeleted() == 1) {
            throw new RuntimeException("项目不存在");
        }
        
        if (project.getStatus() != 1) {
            throw new RuntimeException("项目不在待审批状态");
        }
        
        if (approved) {
            project.setStatus(2); // 已立项
        } else {
            project.setStatus(3); // 已驳回
        }
        
        project.setApproverId(approverId);
        project.setApprovalComment(comment);
        project.setApprovalTime(LocalDateTime.now());
        project.setUpdateTime(LocalDateTime.now());
        
        updateById(project);
        log.info("项目审批完成: {}, 结果: {}", id, approved ? "通过" : "驳回");
        return project;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectLx cancel(Long id) {
        ProjectLx project = getById(id);
        if (project == null || project.getDeleted() == 1) {
            throw new RuntimeException("项目不存在");
        }
        
        if (project.getStatus() == 2) {
            throw new RuntimeException("已立项的项目不能取消");
        }
        
        project.setStatus(4); // 已取消
        project.setUpdateTime(LocalDateTime.now());
        
        updateById(project);
        log.info("项目已取消: {}", id);
        return project;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ProjectLx project = getById(id);
        if (project == null || project.getDeleted() == 1) {
            throw new RuntimeException("项目不存在");
        }
        
        // 逻辑删除
        project.setDeleted(1);
        project.setUpdateTime(LocalDateTime.now());
        updateById(project);
        log.info("项目已删除: {}", id);
    }

    @Override
    public IPage<ProjectLxResponseDTO> pageProjects(Integer pageNum, Integer pageSize, Integer status, String keyword) {
        Page<ProjectLx> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ProjectLx> wrapper = new LambdaQueryWrapper<>();
        
        // 未删除的数据
        wrapper.eq(ProjectLx::getDeleted, 0);
        
        // 状态过滤
        if (status != null) {
            wrapper.eq(ProjectLx::getStatus, status);
        }
        
        // 关键词搜索（项目名称、项目编号、客户名称）
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(ProjectLx::getProjectName, keyword)
                    .or().like(ProjectLx::getProjectCode, keyword)
                    .or().like(ProjectLx::getCustomerName, keyword));
        }
        
        // 按创建时间倒序
        wrapper.orderByDesc(ProjectLx::getCreateTime);
        
        IPage<ProjectLx> projectPage = page(page, wrapper);
        
        // 转换为 DTO
        IPage<ProjectLxResponseDTO> dtoPage = new Page<>();
        BeanUtil.copyProperties(projectPage, dtoPage, "records");
        dtoPage.setRecords(projectPage.getRecords().stream()
                .map(this::convertToDTO)
                .toList());
        
        return dtoPage;
    }

    @Override
    public ProjectLxResponseDTO getProjectDetail(Long id) {
        ProjectLx project = getById(id);
        if (project == null || project.getDeleted() == 1) {
            throw new RuntimeException("项目不存在");
        }
        return convertToDTO(project);
    }

    /**
     * 生成项目编号
     * 格式: LX + 年月日 + 4位流水号
     */
    private String generateProjectCode() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "LX" + dateStr;
        
        // 查询当天最大编号
        LambdaQueryWrapper<ProjectLx> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(ProjectLx::getProjectCode, prefix)
                .orderByDesc(ProjectLx::getProjectCode)
                .last("LIMIT 1");
        
        ProjectLx lastProject = getOne(wrapper);
        
        int sequence = 1;
        if (lastProject != null && lastProject.getProjectCode().startsWith(prefix)) {
            try {
                String lastSeq = lastProject.getProjectCode().substring(prefix.length());
                sequence = Integer.parseInt(lastSeq) + 1;
            } catch (Exception e) {
                sequence = 1;
            }
        }
        
        return prefix + String.format("%04d", sequence);
    }

    /**
     * 转换为 DTO
     */
    private ProjectLxResponseDTO convertToDTO(ProjectLx project) {
        ProjectLxResponseDTO dto = BeanUtil.copyProperties(project, ProjectLxResponseDTO.class);
        
        // 设置状态描述
        if (project.getStatus() != null && project.getStatus() >= 0 && project.getStatus() < STATUS_DESC.length) {
            dto.setStatusDesc(STATUS_DESC[project.getStatus()]);
        }
        
        // 设置优先级描述
        if (project.getPriority() != null && project.getPriority() >= 0 && project.getPriority() < PRIORITY_DESC.length) {
            dto.setPriorityDesc(PRIORITY_DESC[project.getPriority()]);
        }
        
        return dto;
    }
}

