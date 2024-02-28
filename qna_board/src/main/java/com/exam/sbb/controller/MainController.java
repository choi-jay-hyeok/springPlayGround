package com.exam.sbb.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    //서블릿 방식의 응답/요청
    @GetMapping("/plus2")
    public void showPlus2(HttpServletRequest req, HttpServletResponse res) throws IOException {
        int a = Integer.parseInt(req.getParameter("a"));
        int b = Integer.parseInt(req.getParameter("b"));

        res.getWriter().append(a + b + "");
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

    @GetMapping("/mbti/{name}")
    @ResponseBody
    public String showMbti(@PathVariable("name") String name) {
        String rs = switch (name) {
            case "홍길동" -> "INFP";
            case "홍길순" -> "ENFP";
            case "임꺽정" -> "ISTP";
            case "박상원" -> "INFJ";
            default -> "모름";
        };
        return rs;
    }

    @GetMapping("/saveSession/{name}/{value}")
    @ResponseBody
    public String showSession(@PathVariable("name") String name, @PathVariable("value") String value, HttpServletRequest req) {
        HttpSession session = req.getSession();
        session.setAttribute(name, value);
        return "세션변수 %s의 값이 %s(으)로 설정되었습니다.".formatted(name, value);
    }

    @GetMapping("/getSession/{name}")
    @ResponseBody
    public String showSession(@PathVariable("name") String name, /*HttpServletRequest req,*/ HttpSession session) {
        /*HttpSession session = req.getSession(); // req => 쿠키 => JSESSIONID => 세션을 얻을 수 있음,
        * HttpServletRequest를 통하지 않고 바로 HttpSession 을 통해서도 세션 접근 가능*/
        String value = (String) session.getAttribute(name);
        session.setAttribute(name, value);
        return "세션변수 %s의 값은 %s 입니다.".formatted(name, value);
    }

    private List<Article> articles = new ArrayList<>(
            Arrays.asList(
                    new Article("제목", "내용"),
                    new Article("제목", "내용")
            )
    );

    @GetMapping("/addArticle")
    @ResponseBody
    public String addArticle(String title, String body) {
        Article article = new Article(title, body);
        articles.add(article);

        return "%d번 게시물이 생성되었습니다.".formatted(article.getId());
    }

    @GetMapping("/article/{id}")
    @ResponseBody
    public Article getArticle(@PathVariable("id") int id) {
        Article article = articles //id가 1번인 게시물이 앞에서 3번째
                .stream()
                .filter(a -> a.getId() == id)
                .findFirst()
                .orElse(null);

        return article;
    }

    @GetMapping("/modifyArticle/{id}")
    @ResponseBody
    public String modifyArticle(@PathVariable("id")int id, String title, String body) {

        Article article = articles
                .stream()
                .filter(a -> a.getId() == id)
                .findFirst()
                .orElse(null);

        if (article == null) {
            return "%d번 게시물은 존재하지 않습니다.".formatted(id);
        }

        article.setTitle(title);
        article.setBody(body);

        return "%d번 게시물을 수정하였습니다.".formatted(article.getId());
    }

    @GetMapping("/deleteArticle/{id}")
    @ResponseBody
    public String deleteArticle(@PathVariable("id")int id) {

        Article article = articles
                .stream()
                .filter(a -> a.getId() == id)
                .findFirst()
                .orElse(null);

        if (article == null) {
            return "%d번 게시물은 존재하지 않습니다.".formatted(id);
        }

        articles.remove(article);

        return "%d번 게시물을 삭제하였습니다.".formatted(article.getId());
    }

    @GetMapping("/addPerson") //이거 말고
    @ResponseBody
    public Person addPerson(int id, int age, String name) {
        Person p = new Person(id, age, name);
        return p;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    class Article {
        private static int lastId = 0;
        private int id;
        private String title;
        private String body;

        public Article(String title, String body) {
            this(++lastId, title, body);
        }
    }

    @AllArgsConstructor
    class Person {
        private int id;
        private int age;
        private String name;
    }
}
