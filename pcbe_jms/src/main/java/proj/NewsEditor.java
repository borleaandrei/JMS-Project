package proj;

import org.apache.activemq.command.ActiveMQObjectMessage;
import proj.ChangeListener;
import proj.Dispatcher;
import proj.News;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class NewsEditor implements MessageListener, ChangeListener {

    private String editorName;
    private Dispatcher dispatcher;
    private Map<String, News> publishedNews = new HashMap<>(); // news unique identifier && news

    public NewsEditor(String editorName, Dispatcher dispatcher) {
        this.editorName = editorName;
        this.dispatcher = dispatcher;
    }

    public String getEditorName() {
        return editorName;
    }

    @Override
    public void onMessage(Message message) {
        News receivedNews = null;
        try {
            receivedNews = (News)((ActiveMQObjectMessage)message).getObject();
            receivedNews = publishedNews.get(receivedNews.getDescription());
            receivedNews.incrementVisualizationNumb();
            System.out.println("Editor " + editorName + " received message: " + receivedNews.getDescription() + " numbVis: " + receivedNews.getNumberOfVisualizations());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void publishNews(News news, String newsTopic) {
        newsTopic = editorName + "/" + newsTopic;
        news.setNewsTopic(newsTopic);
        System.out.println("DISPATCHER " + dispatcher + " newsTopic " + newsTopic + "  news " + news);
        dispatcher.publishMessage(newsTopic, news);
        news.setChangeListener(this);
        publishedNews.put(news.getDescription(), news);
    }


    @Override
    public void changeDetected(News newsChanged, String oldNewsDescription) {

        newsChanged.setDateOfLastUpdate(System.currentTimeMillis());
        publishedNews.remove(oldNewsDescription);
        publishedNews.put(newsChanged.getDescription(), newsChanged);
        dispatcher.publishMessage(newsChanged.getNewsTopic(), newsChanged);
    }

    public void deleteNews(String newsDescription) {
        News news = publishedNews.remove(newsDescription);

        news.setDateOfLastUpdate(-1);
        dispatcher.publishMessage(news.getNewsTopic(), news);
    }
}
