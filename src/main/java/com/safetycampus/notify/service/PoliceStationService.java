package com.safetycampus.notify.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.safetycampus.notify.dto.PoliceStationDTO;
import com.safetycampus.notify.entity.PoliceStation;

import java.util.List;

public interface PoliceStationService extends IService<PoliceStation> {

    IPage<PoliceStation> selectPage(PoliceStationDTO dto);

    boolean add(PoliceStationDTO dto);

    boolean update(PoliceStationDTO dto);

    boolean delete(Long id);

    PoliceStation getDetail(Long id);

    List<PoliceStation> listAll();
}
