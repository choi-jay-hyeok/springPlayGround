package com.codingrecipe.board.controller;

import com.codingrecipe.board.dto.BoardDTO;
import com.codingrecipe.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    //글 작성 페이지 요청
    @GetMapping("/save")
    public String saveForm() {
        return "save";
    }

    //글 작성 처리
    @PostMapping("/save")
    public String save(@ModelAttribute BoardDTO boardDTO) throws IOException {
        System.out.println("boardDTO = " + boardDTO);
        boardService.save(boardDTO);
        return "redirect:/board/paging";
    }

    //글 전체 목록
    @GetMapping("/")
    public String findAll(Model model) {
        //DB에서 전체 게시글 리스트를 가져와서 list.html에 보여준다.
        List<BoardDTO> boardDTOList = boardService.findAll();
        model.addAttribute("boardList", boardDTOList);
        return "list";
    }

    //글 상세 페이지
    @GetMapping("/{id}")
    public String findById(@PathVariable Long id, Model model,
                           @PageableDefault(page = 1) Pageable pageable) {
        //해당 게시글의 조회수를 1 올리고,
        boardService.updateHits(id);
        //게시글 데이터를 가져와서 detail.html에 출력
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("board", boardDTO);
        //상세 페이지에서 '목록'을 눌렀을 때 해당 상세 페이지가 있던 페이지로 돌아가기 위함(1 페이지로 돌아가지 않고)
        model.addAttribute("page", pageable.getPageNumber());
        return "detail";
    }

    //글 수정 페이지 요청
    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model) {
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("boardUpdate", boardDTO);
        return "update";
    }

    //글 수정 처리
    @PostMapping("/update")
    public String update(@ModelAttribute BoardDTO boardDTO, Model model,
                         @PageableDefault(page = 1) Pageable pageable) {
        BoardDTO board = boardService.update(boardDTO);
        model.addAttribute("board", board);
        model.addAttribute("page", pageable.getPageNumber());
        return "detail";
//        return "redirect:/board/" + boardDTO.getId(); //현재 코드에서는 redirect로 인해 수정을 해도 조회수가 1 올라감
    }

    //글 삭제
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        boardService.delete(id);
        return "redirect:/board/paging";
    }

    //페이징 처리
    //페이지 요청이 올 때, /board/paging?page=1
    @GetMapping("/paging")
    public String paging(@PageableDefault(page = 1)Pageable pageable, Model model) {
//        pageable.getPageNumber();
        //service클래스에서 return boardDTOS를 받아서 boardList에 담음
        Page<BoardDTO> boardList = boardService.paging(pageable);
        int blockLimit = 5; //하단에 나오는 한번에 표시되는 페이지의 개수
        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1; //blockLimit의 첫 숫자
        int endPage = ((startPage + blockLimit - 1) < boardList.getTotalPages()) ? startPage + blockLimit - 1 : boardList.getTotalPages(); //blockLimit의 마지막 숫자

        model.addAttribute("boardList", boardList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "paging";
    }
}

