package com.exam.sbb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class MainController {

    private int increaseNo = -1;

    @RequestMapping("/sbb")
    // 아래 함수의 리턴값을 그대로 브라우저에 표시
    // 아래 함수의 리턴값을 문자열화 해서 브라우저 응답을 바디에 담는다.
    @ResponseBody
    public String index() {
        return "안녕하세요.";
    }

    @GetMapping("/page1")
    @ResponseBody
    public String showGet() {
        return """
                <form method="POST" action="/page2"/>
                    <input type="number" name="age" placeholder="나이 입력" />
                    <input type="submit" value="page2로 POST 방식으로 이동" />
                </form>
                """;
    }

    @GetMapping("/plus")
    @ResponseBody
    public int showPlus(@RequestParam int a, int b) {
        return a + b;
    }

    @GetMapping("/minus")
    @ResponseBody
    public int showMinus(@RequestParam int a, int b) {
        return a - b;
    }

    @GetMapping("/increase")
    @ResponseBody
    public int showIncrease() {
        increaseNo++;
        return increaseNo;
    }

    @PostMapping("/page2")
    @ResponseBody
    public String showPage2Post(@RequestParam(defaultValue = "0") int age) {
        return """
                <h1>입력된 나이 : %d</h1>
                <h1>안녕하세요. POST 방식으로 오신 걸 환영합니다.</h1>
                """.formatted(age);
    }

    @GetMapping("/gugudan")
    @ResponseBody
    public String showGugudan(Integer dan, Integer limit) {
        if (dan == null) {
            dan = 9;
        }
        if (limit == null) {
            limit = 9;
        }

        Integer finalDan = dan;
        return IntStream.rangeClosed(1, limit)
                .mapToObj(i -> "%d * %d = %d".formatted(finalDan, i, finalDan * i))
                .collect(Collectors.joining("<br>"));
    }
}
