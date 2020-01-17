package utils.observer;

import utils.events.ProfesorChangeEvent;

public interface ObservableProfesor<E extends ProfesorChangeEvent> {
    void addObserverProf(ProfesorObserver<E> e);
    void removeObserverProf(ProfesorObserver<E> e);
    void notifyObserversProf(E t);
}