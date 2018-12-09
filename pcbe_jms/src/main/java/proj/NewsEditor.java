package proj;

import proj.ChangeListener;
import proj.Dispatcher;
import proj.News;

import javax.jms.Message;
import javax.jms.MessageListener;
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

    @Override
    public void onMessage(Message message) {
        System.out.println("Editor " + editorName + " received message: " + message);
    }

    public void publishNews(News news, String newsTopic) {
        news.setNewsTopic(newsTopic);
        System.out.println("DISPATCHER " + dispatcher + " newsTopic " + newsTopic + "  news " + news);
        dispatcher.publishMessage(newsTopic, news);
        news.incrementVisualizationNumb();
        news.setChangeListener(this);
        publishedNews.put(news.toString(), news);
    }


    @Override
    public void changeDetected(News newsChanged) {
        //TODO: republish newsChanged
    }
}
