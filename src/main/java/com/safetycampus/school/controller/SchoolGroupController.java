package com.safetycampus.school.controller;

import com.safetycampus.common.result.Result;
import com.safetycampus.school.entity.SchoolGroup;
import com.safetycampus.school.service.SchoolGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/school/group")
@Tag(name = "学校分组管理", description = "学校分组的CRUD操作")
public class SchoolGroupController {

    @Autowired
    private SchoolGroupService schoolGroupService;

    @GetMapping("/list")
    @Operation(summary = "获取所有分组列表")
    public Result<List<SchoolGroup>> list() {
        return Result.success(schoolGroupService.listAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取分组详情")
    public Result<SchoolGroup> getById(@PathVariable Long id) {
        return Result.success(schoolGroupService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增分组")
    public Result<Void> add(@RequestBody SchoolGroup group) {
        boolean result = schoolGroupService.addGroup(group);
        return result ? Result.success() : Result.fail();
    }

    @PutMapping
    @Operation(summary = "编辑分组")
    public Result<Void> update(@RequestBody SchoolGroup group) {
        boolean result = schoolGroupService.updateGroup(group);
        return result ? Result.success() : Result.fail();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除分组")
    public Result<Void> delete(@PathVariable Long id) {
        boolean result = schoolGroupService.deleteGroup(id);
        return result ? Result.success() : Result.fail();
    }
}
