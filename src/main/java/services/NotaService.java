package services;


import domain.Nota;
import repositories.AbstracBaseRepository;
import utils.events.GradeChangeEvent;
import utils.observer.Observer;
import validators.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class NotaService implements Service<String, Nota>/*, Observable<GradeChangeEvent>*/ {
    private AbstracBaseRepository<String, Nota> notaRepository = null;
    private List<Observer<GradeChangeEvent>> observers=new ArrayList<>();

    public NotaService(AbstracBaseRepository notaRepository) {
        this.notaRepository=notaRepository;
    }

    @Override
    public Nota findById(String s) {
        return notaRepository.findOne(s);
    }

    @Override
    public Iterable<Nota> getAll() {
        return notaRepository.findAll();
    }

    @Override
    public Nota add(Nota entity) throws ValidationException { return notaRepository.save(entity); }

    @Override
    public Nota removeById(String s) {
        return notaRepository.delete(s);
    }

    @Override
    public Nota update(Nota entity) {
        return notaRepository.update(entity);
    }

    /*@Override
    public void addObserver(Observer<GradeChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<GradeChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(GradeChangeEvent t) {
        observers.forEach(x->x.update(t));
    }*/
}
