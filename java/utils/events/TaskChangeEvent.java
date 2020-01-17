package utils.events;

import domain.Tema;

public class TaskChangeEvent implements Event {
    private ChangeEventType type;
    private Tema data, oldData;

    public TaskChangeEvent(ChangeEventType type, Tema data){
        this.type = type;
        this.data = data;
    }

    public TaskChangeEvent(ChangeEventType type, Tema data, Tema oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() { return type; }

    public Tema getData() { return data; }

    public Tema getOldData() { return oldData; }
}
