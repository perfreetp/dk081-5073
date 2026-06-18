package com.safetycampus.notify.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.safetycampus.common.result.Result;
import com.safetycampus.notify.dto.ContactBookDTO;
import com.safetycampus.notify.entity.ContactBook;
import com.safetycampus.notify.entity.ContactGroup;
import com.safetycampus.notify.service.ContactBookService;
import com.safetycampus.notify.service.ContactGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "通讯录管理接口", description = "联动通讯录和分组的增删改查管理")
@RestController
@RequestMapping("/api/notify/contact")
public class ContactBookController {

    @Resource
    private ContactBookService contactBookService;

    @Resource
    private ContactGroupService contactGroupService;

    @Operation(summary = "分页查询联系人列表")
    @GetMapping("/page")
    public Result<IPage<ContactBook>> page(ContactBookDTO dto) {
        IPage<ContactBook> page = contactBookService.selectPage(dto);
        return Result.success(page);
    }

    @Operation(summary = "获取联系人详情")
    @GetMapping("/{id}")
    public Result<ContactBook> getDetail(@Parameter(description = "联系人ID") @PathVariable Long id) {
        ContactBook contact = contactBookService.getDetail(id);
        return Result.success(contact);
    }

    @Operation(summary = "新增联系人")
    @PostMapping
    public Result<Boolean> add(@Valid @RequestBody ContactBookDTO dto) {
        boolean result = contactBookService.add(dto);
        return Result.success("新增成功", result);
    }

    @Operation(summary = "修改联系人")
    @PutMapping
    public Result<Boolean> update(@Valid @RequestBody ContactBookDTO dto) {
        boolean result = contactBookService.update(dto);
        return Result.success("修改成功", result);
    }

    @Operation(summary = "删除联系人")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@Parameter(description = "联系人ID") @PathVariable Long id) {
        boolean result = contactBookService.delete(id);
        return Result.success("删除成功", result);
    }

    @Operation(summary = "按分组查询联系人")
    @GetMapping("/group/{groupId}")
    public Result<List<ContactBook>> listByGroup(@Parameter(description = "分组ID") @PathVariable Long groupId) {
        List<ContactBook> list = contactBookService.listByGroupId(groupId);
        return Result.success(list);
    }

    @Operation(summary = "按单位类型查询联系人")
    @GetMapping("/unit-type/{unitType}")
    public Result<List<ContactBook>> listByUnitType(@Parameter(description = "单位类型") @PathVariable Integer unitType) {
        List<ContactBook> list = contactBookService.listByUnitType(unitType);
        return Result.success(list);
    }

    @Operation(summary = "获取值班人员列表")
    @GetMapping("/duty")
    public Result<List<ContactBook>> listDuty() {
        List<ContactBook> list = contactBookService.listDutyContacts();
        return Result.success(list);
    }

    @Operation(summary = "获取所有分组")
    @GetMapping("/group/list")
    public Result<List<ContactGroup>> listGroups() {
        List<ContactGroup> list = contactGroupService.listAll();
        return Result.success(list);
    }

    @Operation(summary = "新增分组")
    @PostMapping("/group")
    public Result<Boolean> addGroup(@Valid @RequestBody ContactGroup group) {
        boolean result = contactGroupService.add(group);
        return Result.success("新增成功", result);
    }

    @Operation(summary = "修改分组")
    @PutMapping("/group")
    public Result<Boolean> updateGroup(@Valid @RequestBody ContactGroup group) {
        boolean result = contactGroupService.update(group);
        return Result.success("修改成功", result);
    }

    @Operation(summary = "删除分组")
    @DeleteMapping("/group/{id}")
    public Result<Boolean> deleteGroup(@Parameter(description = "分组ID") @PathVariable Long id) {
        boolean result = contactGroupService.delete(id);
        return Result.success("删除成功", result);
    }
}
