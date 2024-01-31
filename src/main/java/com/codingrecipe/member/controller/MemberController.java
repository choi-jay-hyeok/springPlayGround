package com.codingrecipe.member.controller;

import com.codingrecipe.member.dto.MemberDTO;
import com.codingrecipe.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller //spring이 관리해주는 객체인 spring bean으로 등록하는 어노테이션
@RequiredArgsConstructor
public class MemberController {

    //@RequiredArgsConstructor 와 함께 사용해서 생성자 주입
    private final MemberService memberService;

    //회원가입 페이지 요청
    @GetMapping("/member/save")
    public String saveForm() {
        return "save";
    }

    //회원가입 처리
    @PostMapping("/member/save")
    public String save(/*@RequestParam("memberEmail") String memberEmail,
                       @RequestParam("memberPassword") String memberPassword,
                       @RequestParam("memberName") String memberName*/
            @ModelAttribute MemberDTO memberDTO) {
        System.out.println("MemberController.save");
//        System.out.println("memberEmail = " + memberEmail + ", memberPassword = " + memberPassword + ", memberName = " + memberName);
        System.out.println("memberDTO = " + memberDTO);
        memberService.save(memberDTO);
        return "login";
    }

    //로그인 페이지 요청
    @GetMapping("/member/login")
    public String loginForm() {
        return "login";
    }

    //로그인 처리
    @PostMapping("/member/login")
    public String login(@ModelAttribute MemberDTO memberDTO, HttpSession session) {

        MemberDTO loginResult = memberService.login(memberDTO);
        if (loginResult != null) {
            //login 성공 시, session에 email 정보 올리기
            session.setAttribute("loginEmail", loginResult.getMemberEmail());
            return "main";
        } else {
            //login 실패
            return "login";
        }
    }

    //전체 회원 리스트
    @GetMapping("/member/")
    public String findAll(Model model) {
        List<MemberDTO> memberDTOList = memberService.findAll();
        //특정 html로 가져갈 데이터가 있다면 Model 사용
        model.addAttribute("memberList", memberDTOList);
        return "list";
    }

    //회원정보 상세 조회
    @GetMapping("/member/{id}")
    public String findById(@PathVariable Long id, Model model) {
        MemberDTO memberDTO = memberService.findById(id);
        model.addAttribute("member", memberDTO);
        return "detail";
    }

    //회원정보 수정 페이지 요청
    @GetMapping("/member/update")
    public String updateForm(HttpSession session, Model model) {
        String myEmail = (String) session.getAttribute("loginEmail");
        MemberDTO memberDTO = memberService.updateForm(myEmail);
        model.addAttribute("updateMember", memberDTO);
        return "update";
    }

    //회원정보 수정 처리
    @PostMapping("/member/update")
    public String update(@ModelAttribute MemberDTO memberDTO) {
        memberService.update(memberDTO);
        return "redirect:/member/" + memberDTO.getId(); //redirect로 하지 않으면 model에 memberDTO를 담아서 넘기지 않았기 때문에 빈 페이지만 뜸
    }

    //회원 삭제
    @GetMapping("/member/delete/{id}")
    public String deleteById(@PathVariable Long id) {
        memberService.deleteById(id);
        return "redirect:/member/"; //위의 수정 처리에서 redirect한 이유와 동일
    }

    //로그아웃
    @GetMapping("/member/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "index";
    }
}
