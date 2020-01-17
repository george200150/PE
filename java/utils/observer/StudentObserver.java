package utils.observer;

import utils.events.StudentChangeEvent;

//public interface StudentObserver<E extends StudentChangeEvent> extends Observer<StudentChangeEvent> {
public interface StudentObserver<E extends StudentChangeEvent> {
    void updateStudent(E e);
}
