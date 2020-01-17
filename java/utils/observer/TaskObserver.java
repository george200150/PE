package utils.observer;

import utils.events.TaskChangeEvent;

//public interface TaskObserver<E extends TaskChangeEvent> extends Observer<TaskChangeEvent> {
public interface TaskObserver<E extends TaskChangeEvent> {
    void updateTask(E e);
}
