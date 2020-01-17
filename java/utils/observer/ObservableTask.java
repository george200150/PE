package utils.observer;

import utils.events.TaskChangeEvent;

public interface ObservableTask<E extends TaskChangeEvent> {
    void addObserverTask(TaskObserver<E> e);
    void removeObserverTask(TaskObserver<E> e);
    void notifyObserversTask(E t);
}