package com.codingrecipe.board.controller;

import com.codingrecipe.board.dto.BoardDTO;
import com.codingrecipe.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    //게시글 작성 페이지 이동
    @GetMapping("/save")
    public String saveForm() {
        return "save";
    }

    //게시글 작성 처리
    @PostMapping("/save")
    public String save(@ModelAttribute BoardDTO boardDTO) {
        int saveResult = boardService.save(boardDTO);
        if (saveResult > 0) {
            return "redirect:/board/";
        } else {
            return "save";
        }
    }

    //게시글 전체 리스트 조회
    @GetMapping("/")
    public String findAll(Model model) {
        List<BoardDTO> boardDTOList = boardService.findAll();
        model.addAttribute("boardList", boardDTOList);
        return "list";
    }

    //조회 수 증가
    @GetMapping
    public String findById(@RequestParam("id") Long id, Model model) {
        boardService.updateHits(id);
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("board", boardDTO);
        return "detail";
    }

    //게시글 삭제
    @GetMapping("/delete")
    public String delete(@RequestParam("id") Long id) {
        boardService.delete(id);
        return "redirect:/board/";
    }

    //게시글 수정 페이지 이동
    @GetMapping("/update")
    public String updateForm(@RequestParam("id") Long id, Model model) {
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("board", boardDTO);
        return "update";
    }

    //게시글 수정 처리
    @PostMapping("/update")
    public String update(@ModelAttribute BoardDTO boardDTO, Model model) {
        boardService.update(boardDTO); //전달받은 boardDTO를 사용해서 게시글 업데이트
        BoardDTO dto = boardService.findById(boardDTO.getId()); //업데이트 된 게시글을 다시 DB에서 가져와서 dto 객체에 담음
        model.addAttribute("board", dto); //화면 board라는 이름으로 dto를 전달
        return "detail";
//        return "redirect:/board?id=" + boardDTO.getId();
    }

    //페이징 처리
    // /board/paging?page=2
    // 처음 페이지 요청은 1페이지를 보여줌
    // @RequestParam의 page 값이 필수는 아님(required = false), page값이 넘어오지 않으면 기본값=1(defaultValue = "1")
    @GetMapping("/paging")
    public String paging(Model model,
                         @RequestParam(value = "page", required = false, defaultValue = "1") int page) {
        System.out.println("page = " + page);
        return "index";
    }
}