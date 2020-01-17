package utils.observer;

import utils.events.GradeChangeEvent;

public interface ObservableGrade<E extends GradeChangeEvent> {
    void addObserverGrade(GradeObserver<E> e);
    void removeObserverGrade(GradeObserver<E> e);
    void notifyObserversGrade(E t);
}