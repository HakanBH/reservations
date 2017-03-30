package polling.configuration.broadcast;

import com.rabbitmq.client.*;
import org.springframework.stereotype.Component;
import polling.model.Reservation;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component
public class PollingConsumer {

    private final static String NEW_RESERVATION_QUEUE_NAME = "new.reservation.queue";
    private final static String DELETED_RESERVATION_QUEUE_NAME = "deleted.reservation.queue";

    private Channel channelNewReservation;
    private Connection connectionNewReservation;
    private Channel channelDeleteReservation;
    private Connection connectionDeleteReservation;
    private ConnectionFactory factory;

    private Reservation r;


    @PostConstruct
    private void createConnection() throws IOException, TimeoutException {
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


    public GetResponse getNewReservation() throws IOException, TimeoutException, ShutdownSignalException {
        channelNewReservation.queueDeclarePassive(NEW_RESERVATION_QUEUE_NAME);
        return channelNewReservation.basicGet(NEW_RESERVATION_QUEUE_NAME, false);
    }

    public GetResponse getDeletedReservation() throws IOException, TimeoutException, ShutdownSignalException {
        channelDeleteReservation.queueDeclarePassive(DELETED_RESERVATION_QUEUE_NAME);
        return channelDeleteReservation.basicGet(DELETED_RESERVATION_QUEUE_NAME, false);
    }


    @PreDestroy
    private void closeConnection() throws IOException, TimeoutException {
        channelNewReservation.close();
        connectionNewReservation.close();
        channelNewReservation.queuePurge(NEW_RESERVATION_QUEUE_NAME);

        channelDeleteReservation.close();
        connectionDeleteReservation.close();
        channelDeleteReservation.queuePurge(DELETED_RESERVATION_QUEUE_NAME);

    }
}
