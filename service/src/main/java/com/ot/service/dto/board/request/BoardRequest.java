package com.ot.service.dto.board.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "게시글수정데이터", name = "PutBoard")
public class PutBoard {

    @Schema(description = "인덱스", example = "")
    private Long id;

    @Schema(description = "제목", example = "Title 2")
    private String title;

    @Schema(description = "내용", example = "Contents 2")
    private String contents;

    @Schema(description = "수정자", example = "2")
    private Long updUserSeq;
}

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "게시글수정데이터", name = "PutBoard")
public class PutBoard {

    @Schema(description = "인덱스", example = "")
    private Long id;

    @Schema(description = "제목", example = "Title 2")
    private String title;

    @Schema(description = "내용", example = "Contents 2")
    private String contents;

    @Schema(description = "수정자", example = "2")
    private Long updUserSeq;
}
