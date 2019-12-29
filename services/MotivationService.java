package services;

import domain.Motivation;
import repositories.AbstracBaseRepository;
import utils.events.ChangeEventType;
import utils.events.MotivationChangeEvent;
import utils.observer.Observable;
import utils.observer.Observer;
import validators.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class MotivationService implements Service<String, Motivation>, Observable<MotivationChangeEvent> {
    private AbstracBaseRepository<String, Motivation> motivationRepository = null;
    private List<Observer<MotivationChangeEvent>> observers = new ArrayList<>();

    public MotivationService(AbstracBaseRepository<String, Motivation> motivationRepository) {
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
            notifyObservers(new MotivationChangeEvent(ChangeEventType.ADD, entity));
        }
        return r;
    }

    @Override
    public Motivation removeById(String s) {
        Motivation r = motivationRepository.delete(s);
        if(r != null) {
            notifyObservers(new MotivationChangeEvent(ChangeEventType.DELETE, r));
        }
        return r;
    }

    @Override
    public Motivation update(Motivation newEntity) {
        Motivation oldStudent = motivationRepository.findOne(newEntity.getId());
        Motivation res = motivationRepository.update(newEntity);
        if(res == null) {
            notifyObservers(new MotivationChangeEvent(ChangeEventType.UPDATE, newEntity, oldStudent));
        }
        return res;
    }

    @Override
    public void addObserver(Observer<MotivationChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<MotivationChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(MotivationChangeEvent t) {
        observers.forEach(x->x.update(t));
    }
}
