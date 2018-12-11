package proj;

import org.apache.activemq.command.ActiveMQObjectMessage;
import proj.Dispatcher;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.io.Serializable;

public class NewsReader implements MessageListener {

    private String readerName;
    private Dispatcher dispatcher;

    public NewsReader(String readerName, Dispatcher dispatcher) {
        this.readerName = readerName;
        this.dispatcher = dispatcher;
    }

    @Override
    public void onMessage(Message message) {
        try {
            News receivedNews = (News)((ActiveMQObjectMessage)message).getObject();
            String update = null;
            if(receivedNews.getDateOfLastUpdate() ==  receivedNews.getDateFirstPublication())
                update = "";
            else if(receivedNews.getDateOfLastUpdate() == -1)
                update = "DELETED";
            else
                update = "UPDATED";
            System.out.println("Reader " + readerName + " received message: " + receivedNews.getDescription() + " " + update);
            sendReadEvent(receivedNews);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void sendReadEvent(News news){
        dispatcher.newsRead(news);
    }
}
