package utils.observer;

import utils.events.MotivationChangeEvent;

public interface MotivationObserver<E extends MotivationChangeEvent> {
    void updateMotivation(E e);
}
