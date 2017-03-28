package polling.configuration.broadcast;

import com.rabbitmq.client.*;
import org.springframework.stereotype.Component;
import polling.model.Reservation;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.TimeoutException;


/**
 * Created by Toncho_Petrov on 1/6/2017.
 */
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
        factory.setHost("");
        factory.setPort(5672);
        factory.setUsername("reservation_admin");
        factory.setPassword("r3s3rv3r0v");

        connectionNewReservation = factory.newConnection();
        channelNewReservation = connectionNewReservation.createChannel();

        connectionDeleteReservation = factory.newConnection();
        channelDeleteReservation = connectionDeleteReservation.createChannel();
    }


    public GetResponse getNewReservation() throws IOException, TimeoutException, ShutdownSignalException {
//        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("");
//        factory.setPort(5672);
//        factory.setUsername("reservation_admin");
//        factory.setPassword("!QWERT^");
//        connectionNewReservation = factory.newConnection();
//        channelNewReservation = connectionNewReservation.createChannel();

        channelNewReservation.queueDeclarePassive(NEW_RESERVATION_QUEUE_NAME);
//        Consumer consumer = new DefaultConsumer(channelNewReservation) {
//            @Override
//            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
//                    throws IOException {
//                String message = new String(body, "UTF-8");
//
//                ObjectMapper mapper = new ObjectMapper();
//                r = mapper.readValue(message,Reservation.class);
//            }
//        };

        // loop that waits for message
//        boolean autoAck = true;
//        return channelNewReservation.basicConsume(NEW_RESERVATION_QUEUE_NAME, autoAck, consumer);

        return channelNewReservation.basicGet(NEW_RESERVATION_QUEUE_NAME, false);
    }

    public GetResponse getDeletedReservation() throws IOException, TimeoutException, ShutdownSignalException {
//        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("");
//        factory.setPort(5672);
//        factory.setUsername("reservation_admin");
//        factory.setPassword("!QWERT^");
//        connectionDeleteReservation = factory.newConnection();
//        connectionDeleteReservation.clearBlockedListeners();
//        channelDeleteReservation = connectionDeleteReservation.createChannel();

        channelDeleteReservation.queueDeclarePassive(DELETED_RESERVATION_QUEUE_NAME);

//        Consumer consumer = new DefaultConsumer(channelDeleteReservation) {
//            @Override
//            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
//                    throws IOException {
//                String message = new String(body, "UTF-8");
//
//                ObjectMapper mapper = new ObjectMapper();
//                r = mapper.readValue(message,Reservation.class);
//                }
//        };
//        return channelDeleteReservation.basicConsume(DELETED_RESERVATION_QUEUE_NAME, true, consumer);
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
