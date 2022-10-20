package com.ot.service.mapper;

import com.ot.service.dto.log.ApiLogHistoryDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LogMapper {
    void insertLog(ApiLogHistoryDto param);
}
