package com.ot.service.mapper;

import com.ot.service.dto.board.request.PostBoard;
import com.ot.service.dto.board.request.PutBoard;
import com.ot.service.dto.board.response.Board;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BoardMapper {
    void insertBoard(PostBoard param);

    void updateBoard(PutBoard param);

    Board selectBoard(@Param("id") Long id);

    List<Board> selectBoardAll();

    void deleteBoard(@Param("id") Long id);
}
