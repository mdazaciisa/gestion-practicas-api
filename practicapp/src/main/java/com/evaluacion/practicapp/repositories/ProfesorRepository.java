package com.evaluacion.practicapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.evaluacion.practicapp.models.Profesor;

public interface ProfesorRepository extends JpaRepository<Profesor, Long> {

}
