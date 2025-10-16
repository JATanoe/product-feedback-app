package com.feedback.web;

import com.feedback.domain.User;
import com.feedback.dto.UserDTO;
import com.feedback.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.feedback.service.UserService.getUserDTO;

@Controller
@RequestMapping("/users")
public class UserController {
    private static final String VIEW_PATH = "user";
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String getUsers(ModelMap model) {
        model.addAttribute("users", userService.findAll());
        return VIEW_PATH + "/index";
    }

    @GetMapping("/{id}")
    public String getUserById(ModelMap model, @PathVariable Long id) {
        model.addAttribute("user", userService.findById(id));
        return VIEW_PATH + "/read";
    }

    @GetMapping("/create")
    public String getUserCreate(ModelMap model) {
        model.addAttribute("user", new UserDTO());
        return VIEW_PATH + "/create";
    }

    @GetMapping("/{id}/update")
    public String getUserUpdate(ModelMap model, @PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return "redirect:/users";
        }
        UserDTO userDto = getUserDTO(user);
        model.addAttribute("user", userDto);
        return VIEW_PATH + "/update";
    }

    @PostMapping("/create")
    public String postUserCreate(ModelMap model, @Valid UserDTO userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userDto);
            return VIEW_PATH + "/create";
        }
        User user = userService.save(userDto);
        return "redirect:/users/" + user.getId() + "/update";
    }

    @PostMapping("/{id}/update")
    public String postUserUpdate(ModelMap model, @PathVariable Long id, @Valid UserDTO userDto, BindingResult bindingResult) {
        userDto.setId(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userDto);
            return VIEW_PATH + "/update";
        }
        User user = userService.save(userDto);
        if (user == null) {
            return "redirect:/users";
        }
        return "redirect:/users/" + user.getId() + "/update";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/users";
    }
}
