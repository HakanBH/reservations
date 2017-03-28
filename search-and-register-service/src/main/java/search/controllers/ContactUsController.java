package search.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import search.model.UserMessage;
import search.repository.UserMessageRepository;

@RestController
@RequestMapping("/contactUs")
public class ContactUsController {

    @Autowired
    UserMessageRepository userMessageRepository;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void saveMessage(@RequestBody UserMessage message) {
        userMessageRepository.save(message);
    }
}
