package utils.observer;

import utils.events.MotivationChangeEvent;

public interface ObservableMotivation<E extends MotivationChangeEvent> {
    void addObserverMotivation(MotivationObserver<E> e);
    void removeObserverMotivation(MotivationObserver<E> e);
    void notifyObserversMotivation(E t);
}