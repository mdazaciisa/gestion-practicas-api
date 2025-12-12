package com.evaluacion.practicapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.evaluacion.practicapp.models.Estudiante;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

}
