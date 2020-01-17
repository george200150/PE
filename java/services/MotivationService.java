package services;

import domain.Motivation;
import repositories.CrudRepository;
import utils.events.ChangeEventType;
import utils.events.MotivationChangeEvent;
import utils.observer.MotivationObserver;
import utils.observer.ObservableMotivation;
import validators.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class MotivationService implements Service<String, Motivation>, ObservableMotivation {
    private CrudRepository<String, Motivation> motivationRepository = null;
    private List<MotivationObserver> observers = new ArrayList<>();

    public MotivationService(CrudRepository<String, Motivation> motivationRepository) {
        this.motivationRepository = motivationRepository;
    }

    @Override
    public Motivation findById(String s) {
        return motivationRepository.findOne(s);
    }

    @Override
    public Iterable<Motivation> getAll() {
        return motivationRepository.findAll();
    }

    @Override
    public Motivation add(Motivation entity) throws ValidationException {
        Motivation r = motivationRepository.save(entity);
        if(r == null) {
            notifyObserversMotivation(new MotivationChangeEvent(ChangeEventType.ADD, entity));
        }
        return r;
    }

    @Override
    public Motivation removeById(String s) {
        Motivation r = motivationRepository.delete(s);
        if(r != null) {
            notifyObserversMotivation(new MotivationChangeEvent(ChangeEventType.DELETE, r));
        }
        return r;
    }

    @Override
    public Motivation update(Motivation newEntity) {
        Motivation oldStudent = motivationRepository.findOne(newEntity.getId());
        Motivation res = motivationRepository.update(newEntity);
        if(res == null) {
            notifyObserversMotivation(new MotivationChangeEvent(ChangeEventType.UPDATE, newEntity, oldStudent));
        }
        return res;
    }

    @Override
    public void addObserverMotivation(MotivationObserver e) {
        observers.add(e);
    }

    @Override
    public void removeObserverMotivation(MotivationObserver e) {
        observers.remove(e);
    }

    @Override
    public void notifyObserversMotivation(MotivationChangeEvent t) {
        observers.forEach(x->x.updateMotivation(t));
    }
}
