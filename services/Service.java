package services;


import domain.Entity;
import validators.ValidationException;

public interface Service<ID, E extends Entity<ID>> {
    E findById(ID id);
    Iterable<E> getAll();
    E add(E entity) throws ValidationException;
    E removeById(ID id);
    E update(E entity);
}
