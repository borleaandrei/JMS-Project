package proj;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;

import javax.jms.*;
import java.io.Serializable;
import java.net.URI;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Dispatcher {

    private BrokerService broker;
    private TopicConnection connection; // conneciton is thread safe
    private TopicConnectionFactory connectionFactory;
    long nrSubscription = 0;
    private ConcurrentMap<String, TopicSession> listenerSessions = new ConcurrentHashMap<>();
    private Random rand = new Random();

    public void startConnection() {
        BrokerService broker = null;
        try {
            broker = BrokerFactory.createBroker(new URI("broker:(tcp://localhost:61616)"));
            broker.start();
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            connectionFactory.setTrustAllPackages(true);
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
            try {
                objectMessage.setObject(message);
            } catch (JMSException e) {
                e.printStackTrace();
            }
            MessageProducer publisher = session.createProducer(topic);
            publisher.send(objectMessage);
            session.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void newsRead(News news) {
        publishMessage(news.getDescription(), news);
    }

    public void subscribe(String newsTopic, NewsEditor source, MessageListener messageListener) throws Exception {
        String topicName;
        if(source != null) {
            topicName = source.getEditorName() + "/" + newsTopic;
        } else {
            topicName = newsTopic;
        }
        //String topicName = newsTopic;
        TopicSession session = listenerSessions.get(topicName);
        if (session == null) {
            session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            listenerSessions.put(topicName, session);
        }
        Topic topic = session.createTopic(topicName);
        MessageConsumer consumer = session.createDurableSubscriber(topic, messageListener.toString() + " " + rand.nextLong(), "", false);
        // the scond parameter has to be unique that's why a random value is added
        consumer.setMessageListener(messageListener);
        nrSubscription++;
    }

    public void subscribeForReadEvents(String newsDescription, NewsEditor newsEditor) throws Exception {
        subscribe(newsDescription, null, newsEditor);
    }

    public void subscribeForUpdates(String newsTopic, NewsEditor source, MessageListener newsReader) throws Exception {
//        subscribe(newsTopic + "/update", source, newsReader);
        subscribe(newsTopic, source, newsReader);
    }

}
