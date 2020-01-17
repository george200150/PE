package utils.observer;

import utils.events.GradeChangeEvent;

//public interface GradeObserver<E extends GradeChangeEvent> extends Observer<GradeChangeEvent> {
public interface GradeObserver<E extends GradeChangeEvent> {
    void updateGrade(E e);
}
