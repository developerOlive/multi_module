package com.ot.service.dto.board.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "게시글데이터", name = "Board")
public class Board {

    @Schema(description = "인덱스", example = "ID")
    private Long id;

    @Schema(description = "제목", example = "Title 2")
    private String title;

    @Schema(description = "내용", example = "Contents 2")
    private String contents;

    @Schema(description = "등록일시", example = "")
    private LocalDateTime regDate;

    @Schema(description = "등록자", example = "ID")
    private Long regUserSeq;

    @Schema(description = "수정일시", example = "ID")
    private LocalDateTime updDate;

    @Schema(description = "수정자", example = "ID")
    private Long updUserSeq;
}
