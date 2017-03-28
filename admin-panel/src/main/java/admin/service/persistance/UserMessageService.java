package admin.service.persistance;

import admin.model.UserMessage;

import java.util.List;

/**
 * Created by Trayan_Muchev on 12/13/2016.
 */
public interface UserMessageService {

    List<UserMessage> findAll();

    List<UserMessage> deleteList(List<Integer> messagesId);
}
