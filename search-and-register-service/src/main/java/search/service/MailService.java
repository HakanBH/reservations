package search.service;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private MailSender mailSender;
    private SimpleMailMessage simpleMailMessage;

    public void setSimpleMailMessage(SimpleMailMessage simpleMailMessage) {
        this.simpleMailMessage = simpleMailMessage;
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMail(String subject, String content, String receiver) {
        SimpleMailMessage message = new SimpleMailMessage(simpleMailMessage);
        message.setSubject(subject);
        message.setText(String.format(simpleMailMessage.getText(), content));
        message.setTo(receiver);
        mailSender.send(message);
    }
}
