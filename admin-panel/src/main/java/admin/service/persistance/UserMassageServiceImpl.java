package admin.service.persistance;

import admin.model.UserMessage;
import admin.repository.UserMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static java.lang.Boolean.TRUE;

/**
 * Created by Trayan_Muchev on 12/13/2016.
 */
@Service("userMessageService")
@Transactional
public class UserMassageServiceImpl implements UserMessageService {

    @Autowired
    UserMessageRepository userMessageRepository;


    @Override
    public List<UserMessage> findAll() {
        return (List<UserMessage>) userMessageRepository.findAll();
    }

    @Override
    public List<UserMessage> deleteList(List<Integer> messagesId) {
        for (Integer id : messagesId) {
            UserMessage entity = userMessageRepository.findOne(id);
            entity.setSeen(TRUE);
            userMessageRepository.save(entity);
        }
        return (List<UserMessage>) userMessageRepository.findAll();
    }
}
