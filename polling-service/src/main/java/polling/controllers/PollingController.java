package polling.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.GetResponse;

import common.jsonwebtoken.AuthTokenDetailsDTO;
import common.jsonwebtoken.JsonWebTokenUtility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

import polling.configuration.broadcast.PollingConsumer;
import polling.model.Reservation;
import polling.model.polling.Polling;
import polling.service.ReservationService;

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

    @MessageMapping("/hello")
    @Scheduled(fixedDelay = 1000)
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
