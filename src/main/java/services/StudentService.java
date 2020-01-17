package services;


import domain.Student;
import repositories.CrudRepository;
import utils.events.ChangeEventType;
import utils.events.StudentChangeEvent;
import utils.observer.Observable;
import utils.observer.Observer;
import validators.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class StudentService implements Service<String, Student>, Observable<StudentChangeEvent> {
    private CrudRepository<String, Student> studentRepository = null;
    private List<Observer<StudentChangeEvent>> observers=new ArrayList<>();

    public StudentService(CrudRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Student findById(String s) {
        return studentRepository.findOne(s);
    }

    @Override
    public Iterable<Student> getAll() {
        return studentRepository.findAll();
    }

    @Override
    public Student add(Student entity) throws ValidationException {
        Student r = studentRepository.save(entity);
        if(r == null) {
            notifyObservers(new StudentChangeEvent(ChangeEventType.ADD, entity));
        }
        return r;
    }

    @Override
    public Student removeById(String s) {
        Student r = studentRepository.delete(s);
        if(r != null) {
            notifyObservers(new StudentChangeEvent(ChangeEventType.DELETE, r));
        }
        return r;
    }

    @Override
    public Student update(Student newEntity) {
        Student oldStudent = studentRepository.findOne(newEntity.getId());
        Student res = studentRepository.update(newEntity);
        if(res == null) {
            notifyObservers(new StudentChangeEvent(ChangeEventType.UPDATE, newEntity, oldStudent));
        }
        return res;
    }





    @Override
    public void addObserver(Observer<StudentChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<StudentChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(StudentChangeEvent t) {
        observers.forEach(x->x.update(t));
    }
}
