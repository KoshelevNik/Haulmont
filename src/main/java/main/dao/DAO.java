package main.dao;

import java.util.List;
import java.util.Optional;

public interface DAO<E, ID> {

    void create(E t);

    Optional<E> read(ID id);

    void update(E t);

    void delete(E t);

    List<E> findAll();
}
