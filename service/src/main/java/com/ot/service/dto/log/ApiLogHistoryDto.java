package com.ot.service.dto.log;

@Getter
@Setter
@Builder
public class ApiLogHistoryDto implements Serializable {

    private Long id;
    private String logTypeCd;
    private String path;
    private String reqMsg;
    private String resMsg;
    private Double durationTime;
    private String isError;
    private String errorCd;
    private String errorMsg;
    private LocalDateTime startDt;
    private LocalDateTime endDt;
    private String clientIpAddr;
    private Long threadId;
}
