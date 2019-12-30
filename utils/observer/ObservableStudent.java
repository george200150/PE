package utils.observer;

import utils.events.StudentChangeEvent;

public interface ObservableStudent<E extends StudentChangeEvent> {
    void addObserverStudent(StudentObserver<E> e);
    void removeObserverStudent(StudentObserver<E> e);
    void notifyObserversStudent(E t);
}
