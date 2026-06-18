package com.safetycampus.notify.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.safetycampus.common.exception.BusinessException;
import com.safetycampus.notify.dto.PoliceStationDTO;
import com.safetycampus.notify.entity.PoliceStation;
import com.safetycampus.notify.mapper.PoliceStationMapper;
import com.safetycampus.notify.service.PoliceStationService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class PoliceStationServiceImpl extends ServiceImpl<PoliceStationMapper, PoliceStation> implements PoliceStationService {

    @Resource
    private PoliceStationMapper policeStationMapper;

    @Override
    public IPage<PoliceStation> selectPage(PoliceStationDTO dto) {
        LambdaQueryWrapper<PoliceStation> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.and(w -> w.like(PoliceStation::getStationName, dto.getKeyword())
                    .or().like(PoliceStation::getStationCode, dto.getKeyword())
                    .or().like(PoliceStation::getLiaison, dto.getKeyword()));
        }
        if (dto.getStatus() != null) {
            wrapper.eq(PoliceStation::getStatus, dto.getStatus());
        }
        wrapper.orderByAsc(PoliceStation::getSortOrder).orderByDesc(PoliceStation::getId);
        return page(dto.buildPage(), wrapper);
    }

    @Override
    public boolean add(PoliceStationDTO dto) {
        LambdaQueryWrapper<PoliceStation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PoliceStation::getStationCode, dto.getStationCode());
        if (count(wrapper) > 0) {
            throw new BusinessException("派出所编码已存在");
        }
        PoliceStation station = new PoliceStation();
        station.setStationCode(dto.getStationCode());
        station.setStationName(dto.getStationName());
        station.setLiaison(dto.getLiaison());
        station.setLiaisonPhone(dto.getLiaisonPhone());
        station.setDutyPhone(dto.getDutyPhone());
        station.setAddress(dto.getAddress());
        station.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        station.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        return save(station);
    }

    @Override
    public boolean update(PoliceStationDTO dto) {
        PoliceStation station = getById(dto.getId());
        if (station == null) {
            throw new BusinessException("派出所不存在");
        }
        if (!station.getStationCode().equals(dto.getStationCode())) {
            LambdaQueryWrapper<PoliceStation> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PoliceStation::getStationCode, dto.getStationCode());
            if (count(wrapper) > 0) {
                throw new BusinessException("派出所编码已存在");
            }
        }
        station.setStationCode(dto.getStationCode());
        station.setStationName(dto.getStationName());
        station.setLiaison(dto.getLiaison());
        station.setLiaisonPhone(dto.getLiaisonPhone());
        station.setDutyPhone(dto.getDutyPhone());
        station.setAddress(dto.getAddress());
        station.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : station.getSortOrder());
        station.setStatus(dto.getStatus() != null ? dto.getStatus() : station.getStatus());
        return updateById(station);
    }

    @Override
    public boolean delete(Long id) {
        PoliceStation station = getById(id);
        if (station == null) {
            throw new BusinessException("派出所不存在");
        }
        return removeById(id);
    }

    @Override
    public PoliceStation getDetail(Long id) {
        return getById(id);
    }

    @Override
    public List<PoliceStation> listAll() {
        LambdaQueryWrapper<PoliceStation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PoliceStation::getStatus, 1);
        wrapper.orderByAsc(PoliceStation::getSortOrder).orderByDesc(PoliceStation::getId);
        return list(wrapper);
    }
}
