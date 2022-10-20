package com.ot.admin.controller;

import com.ot.service.dto.BaseResponse;
import com.ot.service.dto.board.request.PostBoard;
import com.ot.service.dto.board.request.PutBoard;
import com.ot.service.dto.board.response.Board;
import com.ot.service.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * 게시판에 대한 HTTP 요청 처리를 담당합니다.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityRequirement(name = "jwtapi")
@Tag(name = "게시판 API")
public class BoardController {

    private final BoardService boardService;

    @PostMapping(value = "/board")
    @Operation(summary = "게시글 등록", description = "게시글 데이터 요청")
    public ResponseEntity<BaseResponse<?>> insertBoardResource(@RequestBody @Valid PostBoard dto) throws URISyntaxException, InterruptedException {
        Board result = boardService.insertBoard(dto);
        BaseResponse<?> res = new BaseResponse.Builder<>(result, true, HttpStatus.CREATED).build();


        return ResponseEntity.created(new URI("/api/board/" + result.getId())).body(res);
    }

    @PutMapping(value = "/board")
    @Operation(summary = "게시글 수정", description = "게시글 데이터 수정 요청")
    public ResponseEntity<BaseResponse<?>> updateBoardResource(@RequestBody PutBoard dto) {
        Board result = boardService.updateBoard(dto);
        BaseResponse<?> res = new BaseResponse.Builder<>(result, true, HttpStatus.OK).build();

        return ResponseEntity.ok().body(res);
    }

    @GetMapping(value = "/board/{id}")
    @Operation(summary = "게시글 단일 조회", description = "게시글 데이터 단일 조회 요청")
    public ResponseEntity<BaseResponse<?>> selectBoardByIdResource(@PathVariable(name = "id") Long id) {
        Board result = boardService.selectBoardById(id);
        BaseResponse<?> res = new BaseResponse.Builder<>(result, true, HttpStatus.OK).build();

        return ResponseEntity.ok().body(res);
    }

    @GetMapping(value = "/board")
    @Operation(summary = "게시글 리스트 조회", description = "게시글 데이터 리스트 조회 요청")
    public ResponseEntity<BaseResponse<?>> selectBoardAllResource() {
        List<Board> result = boardService.selectBoardAll();
        BaseResponse<?> res = new BaseResponse.Builder<>(result, true, HttpStatus.OK).build();

        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping(value = "/board/{id}")
    @Operation(summary = "게시글 삭제", description = "게시글 데이터 삭제 요청")
    public ResponseEntity<Void> deleteBoardResource(@PathVariable(name = "id") Long id) {
        boardService.deleteBoardById(id);

        return ResponseEntity.noContent().build();
    }
}
