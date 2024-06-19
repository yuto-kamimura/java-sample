package com.example.demo.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

@Controller
public class UserController {

    private final UserRepository repository;

    @Autowired
    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestParam("username") String username, @RequestParam("password") String password, Model model) {
        // ユーザー名とパスワードに基づいて認証処理を実装
        Optional<User> user = repository.findByName(username);

        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return "redirect:/users"; // 認証成功時のリダイレクト先
        } else {
            // 認証失敗の場合、エラーメッセージをモデルに追加してloginビューを返す
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ModelAndView users(ModelAndView mav) {
        mav.setViewName("users");
        mav.addObject("users", repository.findAll());
        return mav;
    }

    @PostMapping("/users/add")
    public String addUser(@RequestParam("name") String name, @RequestParam("password") String password, RedirectAttributes redirectAttributes) {
        Optional<User> existingUser = repository.findByName(name);

        if (existingUser.isPresent()) {
            // ユーザー名が既に存在する場合
            redirectAttributes.addFlashAttribute("error", "このユーザー名は既に使用されています。");
            return "redirect:/users/add"; // ユーザー登録フォームにリダイレクト
        } else {
            // ユーザー名が存在しない場合、新規ユーザを追加
            User newUser = new User(name, password);
            repository.save(newUser);
            redirectAttributes.addFlashAttribute("message", "新規ユーザが追加されました。");
            return "redirect:/users";
        }
    }

    @RequestMapping(value = "/users/{id}/delete", method = RequestMethod.POST)
    public String deleteUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        repository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "ユーザが削除されました。");
        return "redirect:/users";
    }
}
