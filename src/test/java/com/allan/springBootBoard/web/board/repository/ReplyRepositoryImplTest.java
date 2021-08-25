package com.allan.springBootBoard.web.board.repository;

import com.allan.springBootBoard.web.board.domain.Board;
import com.allan.springBootBoard.web.board.domain.Reply;
import com.allan.springBootBoard.web.board.domain.model.ReplyDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.AutoConfigureMybatis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DataJpaTest
@AutoConfigureMybatis
class ReplyRepositoryImplTest {

    Long TEST_REPLY_ID = 1l;
    Long TEST_REPLY_GROUP= 2l;
    Board TEST_BOARD;

    @Autowired
    ReplyRepository replyRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    TestEntityManager testEntityManager;

    /**
     * board, reply Entity 초기화 작업 수행.
     * @param
     * @return
     */
    @BeforeEach
    public void setUp(){
        replyRepository.deleteAll();
        boardRepository.deleteAll();
        TEST_BOARD = createBoard();
    }
    @Test
    public void Reply_Entity_최대_ID값_테스트() throws Exception {
        //given
        Reply TEST_REPLY1 = createReply(TEST_REPLY_ID);
        testEntityManager.persist(TEST_REPLY1);
        Reply TEST_REPLY2 = createReply(2l);
        testEntityManager.persist(TEST_REPLY2);

        //when
        Long maxReplyId = replyRepository.getMaxReplyId();

        //then
        assertThat(maxReplyId, is(3l));
    }

    @Test
    public void 전체_댓글조회_테스트() throws Exception {
        //given
        Reply TEST_REPLY = createReply(TEST_REPLY_ID);
        testEntityManager.persist(TEST_REPLY);

        //when
        List<ReplyDTO> replies = replyRepository.getReplyList(TEST_BOARD.getBoardId());

        //then
        assertThat(replies.size(), is(1));
    }

    @Test
    public void 같은_게시글에_총댓글수_테스트() throws Exception {
        //given
        Reply TEST_REPLY_1 = createReply(TEST_REPLY_ID);
        Reply TEST_REPLY_2 = createReply(2l);
        Reply TEST_REPLY_3 = createReply(3l);
        Reply TEST_REPLY_4 = createReply(4l);
        Reply TEST_REPLY_5 = createReply(5l);

        testEntityManager.persist(TEST_REPLY_1);
        testEntityManager.persist(TEST_REPLY_2);
        testEntityManager.persist(TEST_REPLY_3);
        testEntityManager.persist(TEST_REPLY_4);
        testEntityManager.persist(TEST_REPLY_5);

        //when
        Long replyListCnt = replyRepository.getReplyListCnt(TEST_REPLY_1.getBoard().getBoardId(), TEST_REPLY_GROUP);

        //then
        assertThat(replyListCnt, is(5l));
    }

    @Test
    public void 자식댓글_등록시_기존_그룹내_순서조정시_시작순서_반환_테스트() throws Exception {
        //given
        Reply TEST_REPLY_1 = createReply(1l, 1l, 1l, 0l);
        Reply TEST_REPLY_2 = createReply(2l, 1l, 2l, 1l);
        Reply TEST_REPLY_3 = createReply(3l, 2l, 1l, 0l);
        Reply TEST_REPLY_4 = createReply(4l, 1l, 3l, 1l);

        testEntityManager.persist(TEST_REPLY_1);
        testEntityManager.persist(TEST_REPLY_2);
        testEntityManager.persist(TEST_REPLY_3);
        testEntityManager.persist(TEST_REPLY_4);

        //when
        Long groupOrder = replyRepository.getGroupOrder(TEST_BOARD.getBoardId(), 1l, 2l, 1l);

        //then
        assertThat(groupOrder, is(3l));
    }

    @Test
    public void 자식댓글_등록시_기존_그룹내_순서조정_테스트() throws Exception {
        //given
        Reply TEST_REPLY_1 = createReply(1l, 1l, 1l, 0l);
        Reply TEST_REPLY_2 = createReply(2l, 1l, 2l, 1l);
        Reply TEST_REPLY_3 = createReply(3l, 2l, 1l, 0l);
        Reply TEST_REPLY_4 = createReply(4l, 1l, 3l, 1l);

        testEntityManager.persist(TEST_REPLY_1);
        testEntityManager.persist(TEST_REPLY_2);
        testEntityManager.persist(TEST_REPLY_3);
        testEntityManager.persist(TEST_REPLY_4);

        //when
        replyRepository.updateGroupOrder("tester", LocalDateTime.of(2021, 8, 24, 22, 50, 0), TEST_BOARD.getBoardId(), 1l, 3l);
        Reply updatedReply = replyRepository.findById(4l).get();

        //then
        assertThat(updatedReply.getReplyGroupOrder(), is(4l));
        assertThat(updatedReply.getUpdatedBy(), is("tester"));
        assertThat(updatedReply.getUpdatedDate(), is(LocalDateTime.of(2021, 8, 24, 22, 50, 0)));
    }


    private Reply createReply(Long replyId) {
        return Reply.builder()
                .board(TEST_BOARD)
                .replyId(replyId)
                .replyGroup(TEST_REPLY_GROUP)
                .replyGroupOrder(1l)
                .content("TEST")
                .createdBy("TESTER")
                .createdDate(LocalDateTime.now())
                .build();
    }

    private Reply createReply(Long replyId, Long replyGroup, Long replyGroupOrder, Long depth){
        return Reply.builder()
                .board(TEST_BOARD)
                .replyId(replyId)
                .replyGroup(replyGroup)
                .replyGroupOrder(replyGroupOrder)
                .depth(depth)
                .content("TEST")
                .createdBy("TESTER")
                .createdDate(LocalDateTime.now())
                .build();
    }

    private Board createBoard() {
        Board board = Board.builder()
                .title("테스트 게시글 내용")
                .createdBy("fakeuser")
                .createdDate(LocalDateTime.now())
                .build();
        testEntityManager.persist(board);
        return board;
    }

}