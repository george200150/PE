package services;

import domain.Profesor;
import repositories.CrudRepository;
import utils.events.ChangeEventType;
import utils.events.ProfesorChangeEvent;
import utils.observer.Observable;
import utils.observer.Observer;
import validators.ValidationException;

import java.util.ArrayList;
import java.util.List;


public class ProfesorService implements Service<String, Profesor>, Observable<ProfesorChangeEvent> {
    private CrudRepository<String, Profesor> profesorRepository = null;
    private List<Observer<ProfesorChangeEvent>> observers = new ArrayList<>();

    public ProfesorService(CrudRepository<String, Profesor> profesorRepository) {
        this.profesorRepository = profesorRepository;
    }

    @Override
    public Profesor findById(String s) {
        return profesorRepository.findOne(s);
    }

    @Override
    public Iterable<Profesor> getAll() {
        return profesorRepository.findAll();
    }

    @Override
    public Profesor add(Profesor entity) throws ValidationException {
        Profesor r = profesorRepository.save(entity);
        if (r == null) {
            notifyObservers(new ProfesorChangeEvent(ChangeEventType.ADD, entity));
        }
        return r;
    }

    @Override
    public Profesor removeById(String s) {
        Profesor r = profesorRepository.delete(s);
        if (r != null) {
            notifyObservers(new ProfesorChangeEvent(ChangeEventType.DELETE, r));
        }
        return r;
    }

    @Override
    public Profesor update(Profesor newEntity) {
        Profesor oldStudent = profesorRepository.findOne(newEntity.getId());
        Profesor res = profesorRepository.update(newEntity);
        if (res == null) {
            notifyObservers(new ProfesorChangeEvent(ChangeEventType.UPDATE, newEntity, oldStudent));
        }
        return res;
    }


    @Override
    public void addObserver(Observer<ProfesorChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<ProfesorChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(ProfesorChangeEvent t) {
        observers.forEach(x -> x.update(t));
    }
}
