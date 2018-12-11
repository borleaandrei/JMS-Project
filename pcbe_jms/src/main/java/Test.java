import proj.Dispatcher;
import proj.News;
import proj.NewsEditor;
import proj.NewsReader;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Test {

    static class TopicListener implements MessageListener {

        @Override
        public void onMessage(Message message) {
            System.out.println("LISTENER: " + message);
            //latch.countDown();
        }
    }

    public static void main(String[] args) {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.startConnection();

        NewsReader newsReader = new NewsReader("Jimmy", dispatcher);
        NewsReader newsReader1 = new NewsReader("Danny", dispatcher);
        NewsReader newsReader2 = new NewsReader("Trump", dispatcher);

        NewsEditor newsEditor = new NewsEditor("Adevarul", dispatcher);
        NewsEditor newsEditor1 = new NewsEditor("Minciuna", dispatcher);

        long timeNow = System.currentTimeMillis();
        News news1 = new News("Romania 200", timeNow, timeNow, "source", "someone");
        News news2 = new News("Transilvania e reanexata la Imperiul Austro-Ungar", timeNow, timeNow, "source", "someone");


        try {
            dispatcher.subscribe("patriotism", newsEditor, newsReader);
            dispatcher.subscribe("patriotism", newsEditor, newsReader1);
            dispatcher.subscribe("autonomie", newsEditor1, newsReader2);
            dispatcher.subscribeForReadEvents(news1.getDescription(), newsEditor);
            dispatcher.subscribeForReadEvents(news2.getDescription(), newsEditor1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        newsEditor.publishNews(news1, "patriotism");
        newsEditor1.publishNews(news2, "autonomie");

        news1.setDescription(news1.getDescription() + " cea mai frumoasa tara");
        newsEditor.deleteNews(news1.getDescription());
    }
}
