package com.ot.service.interceptor;


import com.ot.service.dto.log.ApiLogHistoryDto;
import com.ot.service.service.LogService;
import com.ot.service.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    private final LogService logService;
    public LogInterceptor(LogService logService) {
        this.logService = logService;
    }
    private LocalDateTime startTime;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        startTime = LocalDateTime.parse(LocalDateTime.now().format(formatter), formatter);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(req, res, handler, ex);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime endTime = LocalDateTime.parse(LocalDateTime.now().format(formatter), formatter);

        ContentCachingRequestWrapper requestWrapper;
        ContentCachingResponseWrapper responseWrapper = (ContentCachingResponseWrapper) res;
        String body = "";
        try {
            if (req.getContentType() != null && req.getContentType().contains("multipart/form-data")) {
                res.setCharacterEncoding(req.getCharacterEncoding());
            } else {
                requestWrapper = (ContentCachingRequestWrapper) req;
                body = IOUtils.toString(requestWrapper.getContentAsByteArray(), req.getCharacterEncoding());
                req.setAttribute("requestBody", body);
                res.setCharacterEncoding(req.getCharacterEncoding());
            }

            String content = null;
            if (res.getContentType() != null &&
                            (res.getContentType().contains("application/json")
                            || res.getContentType().contains("text/html;charset=UTF-8")
                            || res.getContentType().contains("text/plain")
                            || res.getContentType().contains("application/octet-stream")
                            || res.getContentType().contains("application/zip"))) {

                content = IOUtils.toString(responseWrapper.getContentAsByteArray(), res.getCharacterEncoding());
            }

            try {
                if (StringUtils.isNotEmpty(content)) {
                    ApiLogHistoryDto logDTO = setBasicDto(req, endTime);

                    if (res.getContentType().contains("application/json")) {
                        setRequestAndResponseMsg(req, body, content, logDTO);

                        JSONObject obj = new JSONObject(content);
                        setIsError(responseWrapper, logDTO);
                        setResultCodeAndMessage(logDTO, obj);
                        setThreadId(logDTO);

                        // TODO : durationTime 세팅 추가
                    }

                    String clientIp = CommonUtil.getClientIP(req);
                    logDTO.setLogTypeCd("API");
                    logDTO.setClientIpAddr(clientIp);

                    logService.insertLog(logDTO);
                }
                ;
            } catch (Exception e) {
                log.error("======== LOG INSERT FAIL ======== ", e);
            }

        } catch (Exception e) {
            throw e;
        }
    }

    private static void setRequestAndResponseMsg(HttpServletRequest req, String body, String content, ApiLogHistoryDto logDTO) {
        if (StringUtils.isNotEmpty(req.getContentType()) && req.getContentType().contains("multipart/form-data")) {
            logDTO.setReqMsg("");
            logDTO.setResMsg(content);
        } else {
            logDTO.setReqMsg(body);
            logDTO.setResMsg(content);
        }
    }

    private ApiLogHistoryDto setBasicDto(HttpServletRequest req, LocalDateTime endTime) {
        return ApiLogHistoryDto.builder()
                .startDt(startTime)
                .endDt(endTime)
                .path(req.getServletPath())
                .build();
    }

    private static void setThreadId(ApiLogHistoryDto logDTO) {
        logDTO.setThreadId(Thread.currentThread().getId());
    }

    private static void setResultCodeAndMessage(ApiLogHistoryDto logDTO, JSONObject obj) {
        if (obj.has("resultCode") && obj.get("resultCode") != JSONObject.NULL) {
            logDTO.setErrorCd((String) obj.get("resultCode"));
        }
        if (obj.has("errorMessage") && obj.get("errorMessage") != JSONObject.NULL) {
            logDTO.setErrorMsg((String) obj.get("errorMessage"));
        }
    }

    private static void setIsError(ContentCachingResponseWrapper responseWrapper, ApiLogHistoryDto logDTO) {
        int responseStatus = responseWrapper.getStatus();
        logDTO.setIsError(responseStatus == 200 || responseStatus == 201 ? "false" : "true");
    }
}
