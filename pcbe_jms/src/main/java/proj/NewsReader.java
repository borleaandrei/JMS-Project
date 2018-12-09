package proj;

import proj.Dispatcher;

import javax.jms.Message;
import javax.jms.MessageListener;

public class NewsReader implements MessageListener {

    private String readerName;
    private Dispatcher dispatcher;

    public NewsReader(String readerName, Dispatcher dispatcher) {
        this.readerName = readerName;
        this.dispatcher = dispatcher;
    }

    @Override
    public void onMessage(Message message) {
        System.out.println("Reader " + readerName + " received message: "+message);
    }
}
