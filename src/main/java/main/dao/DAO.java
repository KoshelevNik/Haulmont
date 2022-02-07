package main.dao;

import java.util.List;

public interface DAO<E, ID> {

    void create(E t);

    E read(ID id);

    void update(E t);

    void delete(E t);

    List<E> findAll();
}
