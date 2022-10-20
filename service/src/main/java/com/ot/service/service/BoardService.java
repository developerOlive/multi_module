package com.ot.service.service;

import com.ot.service.config.ErrorCodeEnum;
import com.ot.service.dto.board.request.PostBoard;
import com.ot.service.dto.board.request.PutBoard;
import com.ot.service.dto.board.response.Board;
import com.ot.service.exception.BadRequestException;
import com.ot.service.mapper.BoardMapper;
import com.ot.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 게시판에 관한 비즈니스 로직을 담당합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BoardService {

    private final BoardMapper boardMapper;
    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Board insertBoard(PostBoard param) throws InterruptedException {

        log.info("[Insert Service Call]");

        boardMapper.insertBoard(param);

        return selectBoardById(param.getId());
    }

    @Transactional
    public Board updateBoard(PutBoard param) {

        log.info("[Update Service Call]");

        if (param.getId() == null || boardMapper.selectBoard(param.getId()) == null) {
            throw new BadRequestException(ErrorCodeEnum.INVALID_BOARD_ID);
        }

        boardMapper.updateBoard(param);

        return selectBoardById(param.getId());
    }

    @Transactional(readOnly = true)
    public Board selectBoardById(Long id) {

        log.info("[Select By Id Service Call]");

        Board result = boardMapper.selectBoard(id);
        if (result == null) {
            throw new BadRequestException(ErrorCodeEnum.INVALID_BOARD_ID);
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<Board> selectBoardAll() {

        log.info("[Select All Service Call]");

        return boardMapper.selectBoardAll();
    }

    @Transactional
    public void deleteBoardById(Long id) {

        log.info("[Delete Service Call]");

        if (boardMapper.selectBoard(id) == null) {
            throw new BadRequestException(ErrorCodeEnum.INVALID_BOARD_ID);
        }

        boardMapper.deleteBoard(id);
    }
}
