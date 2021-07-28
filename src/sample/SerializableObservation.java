package sample;

import java.io.Serializable;

public class SerializableObservation implements Serializable {
    private String name;
    private Point position;
    private String key;

    public SerializableObservation(String name, Point position) {
        this.name = name;
        this.position = position;
        key = name + position.getX() + position.getY();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
