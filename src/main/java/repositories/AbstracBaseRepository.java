package repositories;


import domain.Entity;
import validators.AbstractValidator;
import validators.ValidationException;

import java.util.Map;
import java.util.TreeMap;

public class AbstracBaseRepository<ID, E extends Entity<ID>> implements CrudRepository<ID, E>{

    protected Map<ID,E> treeMap = null;
    private AbstractValidator<E> abstractValidator;

    public AbstracBaseRepository(AbstractValidator<E> abstractValidator) {
        this.treeMap = new TreeMap<>();
        this.abstractValidator = abstractValidator;
    }

    /**
     *
     * @param id -the id of the entity to be returned
     * id must not be null
     * @return the entity with the specified id
     * or null - if there is no entity with the given id
     * @throws IllegalArgumentException
     * if id is null.
     */
    @Override
    public E findOne(ID id) {

        if (id == null){
            throw new IllegalArgumentException("ID-ul NU POATE FI NULL");
        }

        for (Map.Entry<ID,E> pereche : treeMap.entrySet()) {
            if(pereche.getKey().equals(id)) {
                return pereche.getValue();
            }
        }
        return null;
    }

    /**
     *
     * @return all entities
     */
    @Override
    public Iterable<E> findAll() {
        return treeMap.values();
    }

    /**
     *
     * @param entity
     * entity must be not null
     * @return null- if the given entity is saved
     * otherwise returns the entity (id already exists)
     * @throws ValidationException
     * if the entity is not valid
     * @throws IllegalArgumentException
     * if the given entity is null. *
     */
    @Override
    public E save(E entity) throws ValidationException {

        if(entity == null){
            throw new IllegalArgumentException("ID NU POATE FI NULL!");
        }

        abstractValidator.validate(entity);
        if (findOne(entity.getId()) != null){//TODO: 15.11.2019 - I had to refactor this, so that GUI will show message
            throw new ValidationException("DUPLICAT GASIT!");
        }

        E  oldValue = treeMap.get(entity.getId());
        if(oldValue == null){
            oldValue = treeMap.put(entity.getId(), entity);//if ok return null; else return Student's already existing value.
        }
        return oldValue;
    }

    /**
     * removes the entity with the specified id
     * @param id
     * id must be not null
     * @return the removed entity or null if there is no entity with the
    given id
     * @throws IllegalArgumentException
     * if the given id is null.
     */
    @Override
    public E delete(ID id) {

        if(id == null){
            throw new IllegalArgumentException("ID-ul nu poate fi NULL!");
        }

        E delReturn = null;
        for (Map.Entry<ID,E> pereche : treeMap.entrySet() ) {
            if(pereche.getKey().equals(id)){
                delReturn = treeMap.remove(id);
                break;
            }
        }
        return delReturn;
    }

    /**
     *
     * @param entity
     * entity must not be null
     * @return null - if the entity is updated,
     * otherwise returns the entity - (e.g id does not
    exist).
     * @throws IllegalArgumentException
     * if the given entity is null.
     * @throws ValidationException
     * if the entity is not valid.
     */
    @Override
    public E update(E entity) {
        if (entity == null){
            throw new IllegalArgumentException("Entitatea nu poate fi NULL!");
        }

        E ntt = findOne(entity.getId());
        if(ntt == null){
            return entity;
        }
        else{
            delete(entity.getId());
            ntt = save(entity);
        }
        return ntt;
    }
}