package com.evaluacion.practicapp.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.evaluacion.practicapp.models.Practica;

public interface PracticaRepository extends JpaRepository<Practica, Long> {

	List<Practica> findByEstudianteIdAndActivoTrue(Long estudianteId);
		List<Practica> findByProfesorIdAndActivoTrue(Long profesorId);
	}
