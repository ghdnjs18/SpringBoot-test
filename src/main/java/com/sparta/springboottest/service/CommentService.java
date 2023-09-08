package com.sparta.springboottest.service;

import com.sparta.springboottest.dto.CommentRequestDto;
import com.sparta.springboottest.dto.CommentResponseDto;
import com.sparta.springboottest.dto.MessageResponseDto;
import com.sparta.springboottest.entity.Board;
import com.sparta.springboottest.entity.Comment;
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
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    public CommentResponseDto createComment(CommentRequestDto requestDto, User user) {
        Long boardId = requestDto.getBoardId();
        Board board = findBoard(boardId);

        Comment comment = new Comment(requestDto, board, user);
        commentRepository.save(comment);

        return new CommentResponseDto(comment);
    }

    @Transactional
    public CommentResponseDto updateComment(Long id, CommentRequestDto requestDto, User user) {
        Comment comment = findComment(id);
        String username = comment.getUser().getUsername();

        if (!username.equals(user.getUsername()) && user.getRole() != UserRoleEnum.ADMIN) {
            throw new IllegalArgumentException("해당 댓글의 작성자만 수정할 수 있습니다.");
        }
        comment.update(requestDto);

        return new CommentResponseDto(comment);
    }

    public ResponseEntity<MessageResponseDto> deleteComment(Long id, User user) {
        Comment comment = findComment(id);
        String username = comment.getUser().getUsername();

        if (!username.equals(user.getUsername()) && user.getRole() != UserRoleEnum.ADMIN) {
            throw new IllegalArgumentException("해당 댓글의 작성자만 삭제할 수 있습니다.");
        }
        commentRepository.delete(comment);

        MessageResponseDto message = new MessageResponseDto("게시물 삭제를 성공했습니다.", HttpStatus.OK.value());
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    private Comment findComment(Long id) {
        return commentRepository.findById(id).orElseThrow(() ->
                new NullPointerException("선택한 댓글은 존재하지 않습니다.")
        );
    }

    private Board findBoard(Long id) {
        return boardRepository.findById(id).orElseThrow(() ->
                new NullPointerException("선택한 게시물은 존재하지 않습니다.")
        );
    }
}
