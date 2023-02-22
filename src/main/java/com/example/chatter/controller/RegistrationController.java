package com.example.chatter.controller;

import com.example.chatter.domain.User;
import com.example.chatter.domain.dto.CaptchaResponseDto;
import com.example.chatter.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Controller
public class RegistrationController {
    private final static String CAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private UserService userService;

    @Value("${recaptcha.secret}")
    private String secret;

    @GetMapping("/registration")
    public String registration(Model model){
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(
            @RequestParam("password2") String passwordConfirm,
            @RequestParam("g-recaptcha-response") String captchaResponse,
            @Valid User user,
            BindingResult bindingResult,
            Model model
    ){

        String url = String.format(CAPTCHA_URL, secret, captchaResponse);
        CaptchaResponseDto response = restTemplate.postForObject(url, Collections.emptyList(), CaptchaResponseDto.class);

        if(!response.isSuccess()){
           model.addAttribute("captchaError", "Fill captcha");
        }

        boolean isConfirmEmpty = passwordConfirm.isEmpty();
        if(isConfirmEmpty){
            model.addAttribute("password2Error", "Password confirmation cannot be empty");
        }
        if(user.getPassword() != null && !user.getPassword().equals(passwordConfirm)){
            model.addAttribute("passwordError", "Password are different!");
        }

        if(isConfirmEmpty || bindingResult.hasErrors() || !response.isSuccess()){
            Map<String, String>  errors = ControllerUtils.getErrors(bindingResult);

            model.mergeAttributes(errors);

            return "registration";
        }

        if (!userService.addUser(user)){
            model.addAttribute("usernameError", "User exists!");
            return "registration";
        }

        return "redirect:/login";
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code){
        boolean isActivated = userService.activateUser(code);

        if(isActivated){
            model.addAttribute("messageType", "success");
            model.addAttribute("message", "User successfully activated");

        }
        else{
            model.addAttribute("messageType", "danger");
            model.addAttribute("message", "Activation code is not found!");
        }

        return "login";
    }

}
