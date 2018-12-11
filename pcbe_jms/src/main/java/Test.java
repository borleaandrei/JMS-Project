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
        NewsReader newsReader2 = new NewsReader("Eva", dispatcher);

        NewsEditor newsEditor = new NewsEditor("Adevarul", dispatcher);

        long timeNow = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
        News news1 = new News("romania", timeNow, timeNow, "source", "someone");

        try {
            dispatcher.subscribe("sports", newsReader);
            dispatcher.subscribe("sports", newsReader1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        newsEditor.publishNews(news1,"sports");
    }
}
