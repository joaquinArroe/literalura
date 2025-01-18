package com.aluracursos.screenmatch.repository;

import com.aluracursos.screenmatch.model.Libro;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends CrudRepository<Libro, Long> {
}
