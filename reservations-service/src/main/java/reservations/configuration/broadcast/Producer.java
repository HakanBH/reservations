package reservations.configuration.broadcast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.stereotype.Component;
import reservations.model.Reservation;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by Toncho_Petrov on 1/6/2017.
 */
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
        factory.setHost("");
        factory.setPort(5672);
        factory.setUsername("reservation_admin");
        factory.setPassword("r3s3rv3r0v");

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

//        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("");
//        factory.setPort(5672);
//        factory.setUsername("reservation_admin");
//        factory.setPassword("!QWERT^");
//        connectionNewReservation = factory.newConnection();

//        channelNewReservation = connectionNewReservation.createChannel();
////        channelDeleteReservation.exchangeBind("destination","source","1111");

        channelNewReservation.queueDeclare(NEW_RESERVATION_QUEUE_NAME, true, false, false, null);

        reservation.setAtomicInt(new AtomicInteger(1111));

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(reservation);

        channelNewReservation.basicPublish("", NEW_RESERVATION_QUEUE_NAME, null, json.getBytes());

    }

    public void sendDeletedReservation(Reservation reservation) throws IOException, TimeoutException {

//        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("");
//        factory.setPort(5672);
//        factory.setUsername("reservation_admin");
//        factory.setPassword("!QWERT^");
//        connectionDeleteReservation = factory.newConnection();
//        channelDeleteReservation = connectionDeleteReservation.createChannel();
////        channelDeleteReservation.exchangeBind("destination","source","1111");


        channelDeleteReservation.queueDeclare(DELETED_RESERVATION_QUEUE_NAME, true, false, false, null);

        reservation.setAtomicInt(new AtomicInteger(1112));

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(reservation);

        channelDeleteReservation.basicPublish("", DELETED_RESERVATION_QUEUE_NAME, null, json.getBytes());
    }

}
