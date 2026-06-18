package com.safetycampus.school.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.safetycampus.common.result.Result;
import com.safetycampus.school.dto.SchoolInfoDTO;
import com.safetycampus.school.dto.SchoolInfoQueryDTO;
import com.safetycampus.school.entity.SchoolInfo;
import com.safetycampus.school.service.SchoolInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/school")
@Tag(name = "学校信息管理", description = "学校信息的CRUD操作")
public class SchoolInfoController {

    @Autowired
    private SchoolInfoService schoolInfoService;

    @GetMapping("/page")
    @Operation(summary = "分页查询学校列表")
    public Result<IPage<SchoolInfo>> page(SchoolInfoQueryDTO queryDTO) {
        return Result.success(schoolInfoService.selectPage(queryDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取学校详情")
    public Result<SchoolInfo> getById(@PathVariable Long id) {
        return Result.success(schoolInfoService.getSchoolDetail(id));
    }

    @PostMapping
    @Operation(summary = "新增学校")
    public Result<Void> add(@Valid @RequestBody SchoolInfoDTO dto) {
        boolean result = schoolInfoService.addSchool(dto);
        return result ? Result.success() : Result.fail();
    }

    @PutMapping
    @Operation(summary = "编辑学校")
    public Result<Void> update(@Valid @RequestBody SchoolInfoDTO dto) {
        boolean result = schoolInfoService.updateSchool(dto);
        return result ? Result.success() : Result.fail();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除学校")
    public Result<Void> delete(@PathVariable Long id) {
        boolean result = schoolInfoService.deleteSchool(id);
        return result ? Result.success() : Result.fail();
    }

    @GetMapping("/list")
    @Operation(summary = "获取所有学校列表")
    public Result<List<SchoolInfo>> list() {
        return Result.success(schoolInfoService.list());
    }

    @GetMapping("/group/type")
    @Operation(summary = "按学校类型分组")
    public Result<Map<Integer, List<SchoolInfo>>> groupByType() {
        return Result.success(schoolInfoService.groupByType());
    }

    @GetMapping("/group/group")
    @Operation(summary = "按分组ID分组")
    public Result<Map<Long, List<SchoolInfo>>> groupByGroup() {
        return Result.success(schoolInfoService.groupByGroup());
    }
}
