package proj;

import proj.ChangeListener;

import java.io.Serializable;

public class News implements Serializable {

    private String description;
    private long dateFirstPublication;
    private long dateOfLastUpdate;
    private String informationSource;
    private String author;
    private String newsTopic;
    private int numberOfVisualizations = 0;
    private ChangeListener changeListener;

    public News(String description, long dateFirstPublication, long dateOfLastUpdate, String informationSource, String author) {
        this.description = description;
        this.dateFirstPublication = dateFirstPublication;
        this.dateOfLastUpdate = dateOfLastUpdate;
        this.informationSource = informationSource;
        this.author = author;
    }

    public void setNewsTopic(String newsTopic) {
        this.newsTopic = newsTopic;
    }

    public void setChangeListener(ChangeListener changeListener){
        this.changeListener = changeListener;
    }

    public String getDescription() {
        return description;
    }

    public long getDateFirstPublication() {
        return dateFirstPublication;
    }

    public long getDateOfLastUpdate() {
        return dateOfLastUpdate;
    }

    public String getInformationSource() {
        return informationSource;
    }

    public String getAuthor() {
        return author;
    }

    public void incrementVisualizationNumb() {
        numberOfVisualizations++;
    }

    public void setDescription(String description) {
        changeListener.changeDetected(this);
        this.description = description;
    }

    public void setInformationSource(String informationSource) {
        changeListener.changeDetected(this);
        this.informationSource = informationSource;
    }
}
