package admin.controllers;

import admin.model.DeleteItems;
import admin.model.UserMessage;
import admin.service.persistance.UserMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
@RestController
@RequestMapping(value = "/messages")
public class ContactUsController {

    @Autowired
    UserMessageService userMessageService;

    @Autowired
    public ContactUsController(UserMessageService userMessageService) {
        this.userMessageService = userMessageService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserMessage> getMessages() {
        return userMessageService.findAll();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public List<UserMessage> deleteList(@RequestBody DeleteItems deleteUserMessage) {
        return userMessageService.deleteList(deleteUserMessage.getItemsId());
    }
}