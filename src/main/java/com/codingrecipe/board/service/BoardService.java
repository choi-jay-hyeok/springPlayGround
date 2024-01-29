package com.codingrecipe.board.service;

import com.codingrecipe.board.dto.BoardDTO;
import com.codingrecipe.board.dto.PageDTO;
import com.codingrecipe.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public int save(BoardDTO boardDTO) {
        return boardRepository.save(boardDTO);
    }

    public List<BoardDTO> findAll() {
        return boardRepository.findAll();
    }

    public BoardDTO findById(Long id) {
        return boardRepository.findById(id);
    }

    public void updateHits(Long id) {
        boardRepository.updateHits(id);
    }

    public void delete(Long id) {
        boardRepository.delete(id);
    }

    public void update(BoardDTO boardDTO) {
        boardRepository.update(boardDTO);
    }

    int pageLimit = 3; //한 페이지에 표시할 글의 개수
    int blockLimit = 3; //하단에 표시할 페이지 번호의 개수

    public List<BoardDTO> pagingList(int page) {
        /*
        1 페이지 당 보여지는 글의 개수: 3개일 경우
            1 page => 0
            2 page => 3
            3 page => 6
            int pagingStart = (page - 1) * 3;
        */
        int pagingStart = (page - 1) * pageLimit;
        //숫자 2개 전달하기 위해 Map 사용
        Map<String, Integer> pagingParams = new HashMap<>();
        pagingParams.put("start", pagingStart);
        pagingParams.put("limit", pageLimit);
        List<BoardDTO> pagingList = boardRepository.pagingList(pagingParams);

        return pagingList;
    }

    public PageDTO pagingParam(int page) {
        //전체 글 개수 조회
        int boardCount = boardRepository.boardCount();
        //전체 페이지 개수 계산(글이 총 10개인 경우 10/3= 3.33333 = > 페이지의 수는 4개가 필요
        int maxPage = (int) (Math.ceil((double) boardCount / pageLimit));
        //시작 페이지 값 계산(1, 4, 7, 10 ~~)
        int startPage = (((int) (Math.ceil((double) page / blockLimit))) - 1) * blockLimit + 1;
        //끝 페이지 값 계산(3, 6, 9, 12, ~~)
        int endPage = startPage + blockLimit - 1;
        if (endPage > maxPage) {
            endPage = maxPage;
        }
        //위에서 얻은 값들을 pageDTO 객체에 담아서 넘겨줌
        PageDTO pageDTO = new PageDTO();
        pageDTO.setPage(page);
        pageDTO.setMaxPage(maxPage);
        pageDTO.setStartPage(startPage);
        pageDTO.setEndPage(endPage);
        return pageDTO;
    }
}
