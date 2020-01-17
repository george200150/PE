package services;


import domain.Nota;
import repositories.CrudRepository;
import utils.events.GradeChangeEvent;
import utils.observer.Observer;
import validators.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class NotaService implements Service<String, Nota>/*, Observable<GradeChangeEvent>*/ {
    private CrudRepository<String, Nota> notaRepository = null;
    private List<Observer<GradeChangeEvent>> observers=new ArrayList<>();

    public NotaService(CrudRepository notaRepository) {
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

}
