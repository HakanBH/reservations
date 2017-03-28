package polling.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.GetResponse;
import common.jsonwebtoken.AuthTokenDetailsDTO;
import common.jsonwebtoken.JsonWebTokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import polling.configuration.broadcast.PollingConsumer;
import polling.model.Reservation;
import polling.model.polling.Polling;
import polling.service.ReservationService;

/**
 * Created by Toncho_Petrov on 1/4/2016.
 */
@RestController
public class PollingController {

    private AuthTokenDetailsDTO detailsDTO;

    @Autowired
    private JsonWebTokenUtility tokenUtility;

    @Autowired
    private SimpMessagingTemplate webSocket;

    private ReservationService reservationService;

    private PollingConsumer consumer;

    public PollingController() {
    }

    @Autowired
    public PollingController(ReservationService reservationService, PollingConsumer consumer) {
        this.consumer = consumer;
        this.reservationService = reservationService;
    }


//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
//    @RequestMapping(value = "/polling", method = RequestMethod.GET)
//    public ResponseEntity<Polling> polling(HttpServletResponse response, HttpServletRequest request) throws IOException, TimeoutException {
//
//        final PollingConsumer consumer = new PollingConsumer();
//        Polling pollingObject = new Polling();
//        ObjectMapper mapper = new ObjectMapper();
//
//        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
//
//        GetResponse newReservationResponse = consumer.getNewReservation();
//        String newReservation= null;
//        if(newReservationResponse != null) {
//            newReservation = new String(newReservationResponse.getBody(), "UTF-8");
//            pollingObject.setNewReservation(mapper.readValue(newReservation,Reservation.class));
//            System.out.println("Add: "+pollingObject.getNewReservation().getId());
//
//        }
//        GetResponse deleteReservationResponse = consumer.getDeletedReservation();
//        String deletedReservation = null;
//        if(deleteReservationResponse != null) {
//            deletedReservation = new String(deleteReservationResponse.getBody(), "UTF-8");
//            pollingObject.setDeletedReservation(mapper.readValue(deletedReservation,Reservation.class));
//            System.out.println("Delete: "+pollingObject.getDeletedReservation().getId());
//
//        }
//
//        return ResponseEntity.status(HttpStatus.OK).body(pollingObject);
//        return ResponseEntity.status(HttpStatus.OK).body(null);
//    }

    @MessageMapping("/hello")
    @Scheduled(fixedDelay = 1000)
//    @SendTo("/topic/greetings")
    public void updateReservation() throws Exception {

        Polling pollingObject = new Polling();
        ObjectMapper mapper = new ObjectMapper();

        GetResponse newReservationResponse = consumer.getNewReservation();
        String newReservation = null;

        GetResponse deleteReservationResponse = consumer.getDeletedReservation();
        String deletedReservation = null;

        if (newReservationResponse != null || deleteReservationResponse != null) {

            if (newReservationResponse != null) {
                newReservation = new String(newReservationResponse.getBody(), "UTF-8");
                pollingObject.setNewReservation(mapper.readValue(newReservation, Reservation.class));

                webSocket.convertAndSend("/topic/greetings", pollingObject);
            }

            if (deleteReservationResponse != null) {
                deletedReservation = new String(deleteReservationResponse.getBody(), "UTF-8");
                pollingObject.setDeletedReservation(mapper.readValue(deletedReservation, Reservation.class));

                webSocket.convertAndSend("/topic/greetings", pollingObject);
            }
        } else {
            webSocket.setSendTimeout(60000);
        }


    }


}
