package com.example.chatter.controller;

import com.example.chatter.domain.Message;
import com.example.chatter.domain.User;
import com.example.chatter.repository.MessageRepository;
import com.example.chatter.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@Controller
public class UserMessageController {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private FileService fileService;

    @GetMapping("/user-messages/{user}")
    public String userMessages(
            @AuthenticationPrincipal User curentUser,
            @PathVariable User user,
            Model model,
            @RequestParam(required = false) Message message
    ){
        Set<Message> messages = user.getMessages();

        model.addAttribute("userChannel", user);
        model.addAttribute("subscriptionsCount", user.getSubscriptions().size());
        model.addAttribute("subscribersCount", user.getSubscribers().size());
        model.addAttribute("isSubscriber", user.getSubscribers().contains(curentUser));
        model.addAttribute("messages", messages);
        model.addAttribute("isCurrentUser", curentUser.equals(user));
        model.addAttribute("message", message);
        return "userMessages";
    }

    @PostMapping("/user-messages/{user}")
    public String updateMessage(
            @AuthenticationPrincipal User curentUser,
            @PathVariable Long user,
            @RequestParam(value = "id", required = false) Message message,
            @RequestParam("text") String text,
            @RequestParam("tag") String tag,
            @RequestParam MultipartFile file

    ) throws IOException {
        if(message==null){
            message = new Message();
            message.setAuthor(curentUser);
        }
        if(message.getAuthor().equals(curentUser)){
            if(!text.isEmpty()){
                message.setText(text);
            }

            if(!tag.isEmpty()){
                message.setTag(tag);
            }

            fileService.saveFile(file, message);
            messageRepository.save(message);
        }
        return "redirect:/user-messages/" + user;
    }

    @PostMapping("/delete-user-messages/{user}")
    public String deleteMessage(
            @PathVariable Long user,
            @RequestParam("message") Long message
    ) {
        messageRepository.deleteById(message);
        return "redirect:/user-messages/" + user;
    }
}
