package reservations.configuration.broadcast;


/**
 * Created by Toncho_Petrov on 1/6/2017.
 */
//@Configuration
//@ComponentScan(basePackages = "reservations.configuration.broadcast")
//@EnableRabbit
public class ReservationRabbitMqConfig {


//    private static final String SIMPLE_MESSAGE_QUEUE = "reservation.queue";
//
//    @Bean
//    public ConnectionFactory connectionFactory() {
//        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
//        connectionFactory.setUsername("reservation_admin");
//        connectionFactory.setPassword("1234");
//        return connectionFactory;
//    }
//
//    @Bean
//    public Queue simpleQueue() {
//        return new Queue(SIMPLE_MESSAGE_QUEUE);
//    }
//
//    @Bean
//    public MessageConverter jsonMessageConverter(){
//        return new JsonMessageConverter();
//    }
//
//    @Bean
//    public RabbitTemplate rabbitTemplate() {
//        RabbitTemplate template = new RabbitTemplate(connectionFactory());
//        template.setRoutingKey(SIMPLE_MESSAGE_QUEUE);
//        template.setMessageConverter(jsonMessageConverter());
//        return template;
//    }
//
//    @Bean
//    public SimpleMessageListenerContainer listenerContainer() {
//        SimpleMessageListenerContainer listenerContainer = new SimpleMessageListenerContainer();
//        listenerContainer.setConnectionFactory(connectionFactory());
//        listenerContainer.setQueues(simpleQueue());
//        listenerContainer.setMessageConverter(jsonMessageConverter());
//        listenerContainer.setMessageListener(new Consumer());
//        listenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
//        return listenerContainer;
//    }
}
