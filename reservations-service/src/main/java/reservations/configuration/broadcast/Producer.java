package reservations.configuration.broadcast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.annotation.PreDestroy;

import reservations.model.Reservation;

@Component
public class Producer {

    private final String NEW_RESERVATION_QUEUE_NAME = "new.reservation.queue";
    private final String DELETED_RESERVATION_QUEUE_NAME = "deleted.reservation.queue";
    private Connection connectionNewReservation;
    private Channel channelNewReservation;
    private Connection connectionDeleteReservation;
    private Channel channelDeleteReservation;
    private ConnectionFactory factory;

    public Producer() throws IOException, TimeoutException {

        factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");

        connectionNewReservation = factory.newConnection();
        channelNewReservation = connectionNewReservation.createChannel();

        connectionDeleteReservation = factory.newConnection();
        channelDeleteReservation = connectionDeleteReservation.createChannel();
    }

    @PreDestroy
    private void closeConnection() throws IOException, TimeoutException {

        channelNewReservation.close();
        connectionNewReservation.close();

        channelDeleteReservation.close();
        connectionDeleteReservation.close();
    }

    public void sendNewReservation(Reservation reservation) throws IOException, TimeoutException {
        channelNewReservation.queueDeclare(NEW_RESERVATION_QUEUE_NAME, true, false, false, null);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(reservation);

        channelNewReservation.basicPublish("", NEW_RESERVATION_QUEUE_NAME, null, json.getBytes());
    }

    public void sendDeletedReservation(Reservation reservation) throws IOException, TimeoutException {
        channelDeleteReservation.queueDeclare(DELETED_RESERVATION_QUEUE_NAME, true, false, false, null);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(reservation);

        channelDeleteReservation.basicPublish("", DELETED_RESERVATION_QUEUE_NAME, null, json.getBytes());
    }
}
