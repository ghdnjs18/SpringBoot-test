package com.sparta.springboottest.service;

import com.sparta.springboottest.dto.*;
import com.sparta.springboottest.entity.Board;
import com.sparta.springboottest.entity.User;
import com.sparta.springboottest.entity.UserRoleEnum;
import com.sparta.springboottest.repository.BoardRepository;
import com.sparta.springboottest.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    public BoardResponseDto createBoard(BoardRequestDto requestDto, User user) {
        Board board = boardRepository.save(new Board(requestDto, user));
        return new BoardResponseDto(board);
    }

    @Transactional(readOnly = true)
    public ItemResponseDto getBoards() {
        ItemResponseDto responseDto = new ItemResponseDto();
        for (BoardResponseDto board : boardRepository.findAllByOrderByModifiedTimeDesc().stream().map(BoardResponseDto::new).toList()) {
            boardSetComment(board, board.getId());
            responseDto.setBoard(board);
        }
        return responseDto;
    }

    @Transactional(readOnly = true)
    public BoardResponseDto getBoard(Long id) {
        Board board = findBoard(id);
        BoardResponseDto responseDto = new BoardResponseDto(board);
        boardSetComment(responseDto, id);

        return responseDto;
    }

    @Transactional
    public BoardResponseDto updateBoard(Long id, BoardRequestDto requestDto, User user) {
        Board board = findBoard(id);
        String username = board.getUser().getUsername();

        if (!username.equals(user.getUsername()) && user.getRole() != UserRoleEnum.ADMIN) {
            throw new IllegalArgumentException("해당 게시물의 작성자만 수정할 수 있습니다.");
        }
        board.update(requestDto);

        return new BoardResponseDto(board);
    }

    public ResponseEntity<MessageResponseDto> deleteBoard(Long id, User user) {
        Board board = findBoard(id);
        String username = board.getUser().getUsername();

        if (!username.equals(user.getUsername()) && user.getRole() != UserRoleEnum.ADMIN) {
            throw new IllegalArgumentException("해당 게시물의 작성자만 삭제할 수 있습니다.");
        }
        boardRepository.delete(board);

        MessageResponseDto message = new MessageResponseDto("게시물 삭제를 성공했습니다.", HttpStatus.OK.value());
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    // 게시물 검색
    private Board findBoard(Long id) {
        return boardRepository.findById(id).orElseThrow(() ->
                new NullPointerException("선택한 게시물은 존재하지 않습니다.")
        );
    }

    // Board에 Comment 리스트 넣기
    private void boardSetComment(BoardResponseDto board, Long id) {
        for (CommentResponseDto comment : commentRepository.findByBoard_idOrderByModifiedTimeDesc(id).stream().map(CommentResponseDto::new).toList()) {
            board.setComment(comment);
        }
    }
}
