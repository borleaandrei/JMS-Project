import org.apache.activemq.ActiveMQConnectionFactory;
import proj.News;

import javax.jms.*;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.CountDownLatch;

public class JmsProducer {
    private static CountDownLatch latch = new CountDownLatch(1);


    static class TopicListener implements MessageListener {

        @Override
        public void onMessage(Message message) {
            try {
                System.out.println("LISTENER: " + ((ObjectMessage)message).getObject());
            } catch (JMSException e) {
                e.printStackTrace();
            }
            //latch.countDown();
        }
    }

    public static void main(String[] args) throws URISyntaxException, Exception {
        TopicConnection connection = null;
        TopicSession producerSession = null;
        TopicSession consumerSession = null;
        try {
            // Producer
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                "tcp://localhost:61616");
            connectionFactory.setTrustAllPackages(true);
            connection = connectionFactory.createTopicConnection();
            //connection.setClientID("DurabilityTest");
            connection.start();
            producerSession = connection.createTopicSession(false,
                Session.AUTO_ACKNOWLEDGE);
            consumerSession = connection.createTopicSession(false,
                Session.AUTO_ACKNOWLEDGE);

            Topic producerTopic = producerSession.createTopic("customerTopic");
            Topic consumerTopic = producerSession.createTopic("customerTopic");

            // Publish
            String payload = "Task";
            ObjectMessage obj;
            TextMessage msg = producerSession.createTextMessage(payload);
            ObjectMessage objectMessage = producerSession.createObjectMessage();
            objectMessage.setObject(new News("description",
                LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond(),
                LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond(), "source", "author"));

            MessageProducer publisher = producerSession.createProducer(producerTopic);
            System.out.println("Sending text '" + payload + "'");
            //publisher.send(msg, javax.jms.DeliveryMode.PERSISTENT, javax.jms.Message.DEFAULT_PRIORITY, Message.DEFAULT_TIME_TO_LIVE);
            //publisher.send(objectMessage);
            // Consumer1 subscribes to customerTopic
            MessageConsumer consumer1 = producerSession.createDurableSubscriber(producerTopic, "consumer1", "", false);
            TopicListener topicListener1 = new TopicListener();
            consumer1.setMessageListener(topicListener1);

//            MessageConsumer consumer2 = session.createDurableSubscriber(topic, "consumer2", "", false);
//            TopicListener topicListener2 = new TopicListener();
//            consumer2.setMessageListener(topicListener2);


            // Consumer2 subscribes to customerTopic
            //MessageConsumer consumer2 = session.createDurableSubscriber(topic, "consumer2", "", false);

            ///////////////////connection.start();
            //latch.await();

            //publisher.send(objectMessage);
//            obj = (ObjectMessage) consumer1.receive();
//            System.out.println("Consumer1 receives " + obj.getObject());


            //obj = (ObjectMessage) consumer2.receive();
            //System.out.println("Consumer2 receives " + obj.getObject());

            //consumerSession.close();
            publisher.send(objectMessage);

        } finally {
//            if (producerSession != null) {
//                producerSession.close();
//            }
//            if (consumerSession != null) {
//                consumerSession.close();
//            }

//            if (connection != null) {
//                connection.close();
//            }
        }

    }
}