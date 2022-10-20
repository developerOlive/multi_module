package com.ot.service.service;

import com.ot.service.dto.log.ApiLogHistoryDto;
import com.ot.service.mapper.LogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 로그에 대한 비즈니스 로직을 담당합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LogService {

    private final LogMapper logMapper;

    @Async
    public void insertLog(ApiLogHistoryDto param) {

        log.info("[Insert Log Service Call]");

        logMapper.insertLog(param);
    }
}
