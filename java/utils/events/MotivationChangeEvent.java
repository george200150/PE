package utils.events;

import domain.Motivation;

public class MotivationChangeEvent implements Event {
    private ChangeEventType type;
    private Motivation data, oldData;

    public MotivationChangeEvent(ChangeEventType type, Motivation data){
        this.type = type;
        this.data = data;
    }

    public MotivationChangeEvent(ChangeEventType type, Motivation data, Motivation oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() { return type; }

    public Motivation getData() { return data; }

    public Motivation getOldData() { return oldData; }
}
