package com.team03.monew.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.team03.monew.dto.comment.response.CommentLikeDto;
import com.team03.monew.entity.Comment;
import com.team03.monew.entity.CommentLike;
import com.team03.monew.entity.NewsArticle;
import com.team03.monew.entity.User;
import com.team03.monew.repository.CommentLikeRepository;
import com.team03.monew.repository.CommentRepository;
import com.team03.monew.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentLike commentLike;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentLikeRepository commentLikeRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // 필요시 기본 세팅
    }

    @Test
    void 좋아요기능_추가잘되는지_확인() {

        Long commentId = 1L;
        Long userId = 100L;
        Long commentUserId = 10L;

        NewsArticle mockNews = NewsArticle.builder()
                .id(10L)
                .title("뉴스 제목")
                .build();

        User user = User.builder()
                .id(userId)
                .nickname("좋아요누룬사람")
                .build();

        User commentAuthor = User.builder()
                .id(commentUserId)
                .nickname("글작성자")
                .build();

        Comment comment = Comment.builder()
                .id(commentId)
                .content("테스트 댓글")
                .user(commentAuthor)
                .news(mockNews)
                .build();


        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(commentLikeRepository.save(Mockito.any(CommentLike.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        CommentLikeDto result = commentService.commentLikes(commentId, userId);

        // then
        assertNotNull(result);
        assertTrue(result.likedBy().equals(user));
        assertEquals(1, result.commentLikeCount());
    }
}
