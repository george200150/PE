package utils.observer;

import utils.events.ProfesorChangeEvent;

public interface ProfesorObserver<E extends ProfesorChangeEvent> {
    void updateProf(E e);
}