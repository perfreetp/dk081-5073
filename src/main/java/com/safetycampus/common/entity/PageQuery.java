package com.safetycampus.common.entity;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "分页查询参数")
public class PageQuery implements Serializable {

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "排序字段")
    private String orderBy;

    @Schema(description = "排序方向:asc-升序,desc-降序", example = "desc")
    private String orderDir = "desc";

    @JsonIgnore
    public <T> IPage<T> buildPage() {
        Page<T> page = new Page<>(pageNum, pageSize);
        List<OrderItem> orderItems = buildOrderItems();
        if (!orderItems.isEmpty()) {
            page.setOrders(orderItems);
        } else {
            page.addOrder(OrderItem.desc("id"));
        }
        return page;
    }

    @JsonIgnore
    private List<OrderItem> buildOrderItems() {
        List<OrderItem> orderItems = new ArrayList<>();
        if (orderBy != null && !orderBy.isEmpty()) {
            String[] fields = orderBy.split(",");
            String[] dirs = orderDir.split(",");
            for (int i = 0; i < fields.length; i++) {
                String field = fields[i].trim();
                boolean isAsc = i < dirs.length ? "asc".equalsIgnoreCase(dirs[i].trim()) : "desc".equalsIgnoreCase(orderDir);
                if (isAsc) {
                    orderItems.add(OrderItem.asc(field));
                } else {
                    orderItems.add(OrderItem.desc(field));
                }
            }
        }
        return orderItems;
    }
}
