package com.evaluacion.practicapp.services;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.evaluacion.practicapp.models.Estudiante;
import com.evaluacion.practicapp.models.Practica;
import com.evaluacion.practicapp.models.Profesor;
import com.evaluacion.practicapp.repositories.EstudianteRepository;
import com.evaluacion.practicapp.repositories.PracticaRepository;
import com.evaluacion.practicapp.repositories.ProfesorRepository;
import jakarta.transaction.Transactional;

@Service
public class PracticaService {
    @Autowired
    private PracticaRepository practicaRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private ProfesorRepository profesorRepository;

    // listar prácticas
    public List<Practica> listarPracticas() {
        return practicaRepository.findAll();
    }

    // listar prácticas por estudiante
    public List<Practica> listarPracticasPorEstudiante(Long estudianteId) {
        return practicaRepository.findByEstudianteIdAndActivoTrue(estudianteId);
    }

    // listar prácticas por profesor
    public List<Practica> listarPracticasPorProfesor(Long profesorId) {
        return practicaRepository.findByProfesorIdAndActivoTrue(profesorId);
    }

    // listar por id
    public Practica obtenerPractica(Long id) {
        return practicaRepository.findById(id).orElse(null);
    }

    // crear práctica
    @Transactional
    public Practica crearPractica(
            LocalDate fechaini,
            LocalDate fechafin,
            String descripcion,
            String empresa,
            String jefePractica,
            Long estudianteId,
            Long profesorId) {

        // Validaciones
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        Profesor profesor = profesorRepository.findById(profesorId)
            .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        // crear práctica
        Practica practica = new Practica();
        practica.setFechaini(fechaini);
        practica.setFechafin(fechafin);
        practica.setDescripcion(descripcion);
        practica.setEmpresa(empresa);
        practica.setJefePractica(jefePractica);

        practica.setEstudiante(estudiante);
        practica.setProfesor(profesor);

        return practicaRepository.save(practica);
    }

    // actualizar práctica (sólo profesores)
    @Transactional
    public Practica actualizarPractica(Long id, Practica datosPractica) {
        Practica practica = practicaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Práctica no encontrada"));

        practica.setDescripcion(datosPractica.getDescripcion());
        practica.setFechaini(datosPractica.getFechaini());
        practica.setFechafin(datosPractica.getFechafin());
        practica.setEmpresa(datosPractica.getEmpresa());
        practica.setJefePractica(datosPractica.getJefePractica());
        return practicaRepository.save(practica);
    }

    // eliminar práctica (sólo profesores)
    public void eliminarPractica(Long id) {
        Practica practica = practicaRepository.findById(id)
                .orElse(null);

        if (practica == null) {
            return;
        }

        // Romper relaciones para solucionar error
        // org.hibernate.TransientObjectException:
        if (practica.getEstudiante() != null && practica.getEstudiante().getPracticas() != null) {
            practica.getEstudiante().getPracticas().remove(practica);
        }

        if (practica.getProfesor() != null) {
            practica.setProfesor(null);
        }
        
        practicaRepository.delete(practica);
    }

    //cambiar estado activo/inactivo de prácticas
    public Practica guardar(Practica practica) {
        return practicaRepository.save(practica);
    }

}
