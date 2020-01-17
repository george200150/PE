package services;


import domain.Tema;
import repositories.CrudRepository;
import utils.events.ChangeEventType;
import utils.events.TaskChangeEvent;
import utils.observer.Observable;
import utils.observer.Observer;
import validators.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class TemaService implements Service<String, Tema>, Observable<TaskChangeEvent> {
    private CrudRepository<String, Tema> temaRepository = null;
    private List<Observer<TaskChangeEvent>> observers=new ArrayList<>();

    public TemaService(CrudRepository temaRepository) {
        this.temaRepository = temaRepository;
    }

    @Override
    public Tema findById(String s) {
        return temaRepository.findOne(s);
    }

    @Override
    public Iterable<Tema> getAll() {
        return temaRepository.findAll();
    }

    @Override
    public Tema add(Tema entity) throws ValidationException {
        Tema t = temaRepository.save(entity);
        if (t == null){
            notifyObservers(new TaskChangeEvent(ChangeEventType.ADD, entity));
        }
        return t;
    }

    @Override
    public Tema removeById(String s) {
        Tema r = temaRepository.delete(s);
        if (r != null){
            notifyObservers(new TaskChangeEvent(ChangeEventType.DELETE, r));
        }
        return r;
    }

    @Override
    public Tema update(Tema newEntity) {
        Tema oldTask = temaRepository.findOne(newEntity.getId());
        Tema res = temaRepository.update(newEntity);
        if(res == null) {
            notifyObservers(new TaskChangeEvent(ChangeEventType.UPDATE, newEntity, oldTask));
        }
        return res;
    }



    @Override
    public void addObserver(Observer<TaskChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<TaskChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(TaskChangeEvent t) {
        observers.forEach(x->x.update(t));
    }
}
