package utils.events;

import domain.Nota;

public class GradeChangeEvent implements Event {
    private ChangeEventType type;
    private Nota data, oldData;

    public GradeChangeEvent(ChangeEventType type, Nota data){
        this.type = type;
        this.data = data;
    }

    public GradeChangeEvent(ChangeEventType type, Nota data, Nota oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() { return type; }

    public Nota getData() { return data; }

    public Nota getOldData() { return oldData; }
}
