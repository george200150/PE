package utils.events;

import domain.Profesor;

public class ProfesorChangeEvent implements Event {

    private ChangeEventType type;
    private Profesor data, oldData;

    public ProfesorChangeEvent(ChangeEventType type, Profesor data){
        this.type = type;
        this.data = data;
    }

    public ProfesorChangeEvent(ChangeEventType type, Profesor data, Profesor oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() { return type; }

    public Profesor getData() { return data; }

    public Profesor getOldData() { return oldData; }
}
