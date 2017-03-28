package polling.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import polling.configuration.broadcast.PollingConsumer;

/**
 * Created by Toncho Petrov on 08-Jan-17.
 */


@Controller
public class WebSocketMessageHandlingController {

    @Autowired
    private PollingConsumer pollingConsumer;


//    @MessageMapping("/hello")
//    @SendTo("/topic/greetings")
//    public ResponseEntity sendNewReservation(String s) throws Exception {
//
//        if(s != null || s.equals("")){
//            System.out.println(s);
//        }
//        if(pollingConsumer.getNewReservation() != null){
//            System.out.println("sendNewReservation socketJS: ");
//            return ResponseEntity.status(HttpStatus.SWITCHING_PROTOCOLS).body(pollingConsumer.getNewReservation());
//        }else {
//            return ResponseEntity.status(HttpStatus.CONTINUE).body("Blq !");
//        }
//    }

//    @Scheduled(fixedDelay = 1000)
//    @MessageMapping("/chat")
//    @SendTo("/reservation/delete")
//    public Object sendDeletedReservation() throws Exception {
//        if(pollingConsumer.getDeletedReservation() != null){
//            return pollingConsumer.getDeletedReservation();
//        }else {
//            return null;
//        }
//    }


}
