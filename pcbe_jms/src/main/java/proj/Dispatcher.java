package proj;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;

import javax.jms.*;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Dispatcher {

    private BrokerService broker;
    private TopicConnection connection; // conneciton is thread safe
    private TopicConnectionFactory connectionFactory;

    private ConcurrentMap<String, TopicSession> listenerSessions = new ConcurrentHashMap<>();

    public void startConnection() {
        BrokerService broker = null;
        try {
            broker = BrokerFactory.createBroker(new URI("broker:(tcp://localhost:61616)"));
            broker.start();
            TopicConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            connection = connectionFactory.createTopicConnection();
            connection.setClientID("DurabilityTest");
            connection.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void publishMessage(String newsTopic, News message) {
        TopicSession session;
        Topic topic;
        try {
            session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            topic = session.createTopic(newsTopic);

            ObjectMessage objectMessage = session.createObjectMessage();
            objectMessage.setObject(message);

            MessageProducer publisher = session.createProducer(topic);
            publisher.send(objectMessage);
            session.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(String newsTopic, MessageListener newsReader) throws Exception {
        TopicSession session = listenerSessions.get(newsTopic);
        if (session == null) {
            System.out.println("session was null");
            session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            listenerSessions.put(newsTopic, session);
        }
        Topic topic = session.createTopic(newsTopic);
        MessageConsumer consumer = session.createDurableSubscriber(topic, newsReader.toString(), "", false);
        consumer.setMessageListener(newsReader);
    }

}
