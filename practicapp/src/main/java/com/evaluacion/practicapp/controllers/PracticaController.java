package com.evaluacion.practicapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.evaluacion.practicapp.models.Practica;
import com.evaluacion.practicapp.repositories.EstudianteRepository;
import com.evaluacion.practicapp.repositories.ProfesorRepository;
import com.evaluacion.practicapp.requests.CrearPracticaRequest;
import com.evaluacion.practicapp.responses.PracticaResponse;
import com.evaluacion.practicapp.responses.PracticasResponse;
import com.evaluacion.practicapp.services.PracticaService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api")
public class PracticaController {
    @Autowired
    private PracticaService practicaService;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private ProfesorRepository profesorRepository;

    // listar
    @GetMapping(value = "listar", produces = "application/json")
    public ResponseEntity<Object> listarPracticas(
            @RequestHeader("USER-ID") Long userId,
            @RequestHeader("ROL") String role) {

        PracticasResponse practicasResponse = new PracticasResponse();

        if ("ESTUDIANTE".equalsIgnoreCase(role)) {
            //validar que existe estudiante:
            boolean existe = estudianteRepository.existsById(userId);
            if (!existe) {
                practicasResponse.setStatus(404);
                practicasResponse.setMensaje("ID de estudiante no encontrado");
                practicasResponse.setDatos(null);
                return ResponseEntity.ok().body(practicasResponse);
            }

            practicasResponse.setStatus(200);
            practicasResponse.setMensaje("Prácticas del estudiante");
            practicasResponse.setDatos(practicaService.listarPracticasPorEstudiante(userId));
            return ResponseEntity.ok().body(practicasResponse);
        }

        if ("PROFESOR".equalsIgnoreCase(role)) {
            //validar que existe profesor:
            boolean existe = profesorRepository.existsById(userId);
            if (!existe) {
                practicasResponse.setStatus(404);
                practicasResponse.setMensaje("ID de profesor no encontrado");
                practicasResponse.setDatos(null);
                return ResponseEntity.ok().body(practicasResponse);
            }
            
            practicasResponse.setStatus(200);
            practicasResponse.setMensaje("Prácticas del profesor");
            practicasResponse.setDatos(practicaService.listarPracticasPorProfesor(userId));
            return ResponseEntity.ok().body(practicasResponse);
        }

        practicasResponse.setStatus(403);
        practicasResponse.setMensaje("Rol no autorizado");
        practicasResponse.setDatos(null);
        return ResponseEntity.status(403).body(practicasResponse);
    }

    // listar por id
    @GetMapping(value = "listar/{id}", produces = "application/json")
    public ResponseEntity<Object> obtenerPractica(
            @PathVariable Long id,
            @RequestHeader("USER-ID") Long userId,
            @RequestHeader("ROL") String role) {

        Practica practica = practicaService.obtenerPractica(id);
        PracticaResponse practicaResponse = new PracticaResponse();

        if (practica == null || !practica.isActivo()) {
            practicaResponse.setStatus(404);
            practicaResponse.setMensaje("Práctica no encontrada");
            practicaResponse.setDatos(null);
            return ResponseEntity.ok().body(practicaResponse);
        }

        // Estudiante solo puede ver su propia práctica
        if ("ESTUDIANTE".equalsIgnoreCase(role)) {
            if (practica.getEstudiante() == null || !userId.equals(practica.getEstudiante().getId())) {
                practicaResponse.setStatus(403);
                practicaResponse.setMensaje("No tiene permiso para ver esta práctica");
                practicaResponse.setDatos(null);
                return ResponseEntity.status(403).body(practicaResponse);
            }
        }

        // Profesor solo puede ver prácticas asociadas a él
        if ("PROFESOR".equalsIgnoreCase(role)) {
            if (practica.getProfesor() == null || !userId.equals(practica.getProfesor().getId())) {
                practicaResponse.setStatus(403);
                practicaResponse.setMensaje("No tiene permiso para ver esta práctica");
                practicaResponse.setDatos(null);
                return ResponseEntity.status(403).body(practicaResponse);
            }
        }
        practicaResponse.setStatus(200);
        practicaResponse.setMensaje("Práctica encontrada");
        practicaResponse.setDatos(practica);

        return ResponseEntity.ok().body(practicaResponse);
    }

    // crear
    @PostMapping(value = "crear", produces = "application/json")
    public ResponseEntity<Object> crearPractica(
            @RequestBody CrearPracticaRequest request,
            @RequestHeader("USER-ID") Long userId,
            @RequestHeader("ROL") String role) {

        PracticaResponse practicaResponse = new PracticaResponse();

        // Estudiante solo puede crear prácticas para sí mismo
        if ("ESTUDIANTE".equalsIgnoreCase(role)) {
            if (request.getEstudianteId() == null || !userId.equals(request.getEstudianteId())) {
                practicaResponse.setStatus(403);
                practicaResponse.setMensaje("El estudiante solo puede crear su propia práctica");
                practicaResponse.setDatos(null);
                return ResponseEntity.status(403).body(practicaResponse);
            }
        } else if ("PROFESOR".equalsIgnoreCase(role)) {
            // Profesor solo puede crear prácticas asociadas a sí mismo
            if (request.getProfesorId() == null || !userId.equals(request.getProfesorId())) {
                practicaResponse.setStatus(403);
                practicaResponse.setMensaje("El profesor solo puede crear prácticas asociadas a su propio id");
                practicaResponse.setDatos(null);
                return ResponseEntity.status(403).body(practicaResponse);
            }
        } else {
            practicaResponse.setStatus(403);
            practicaResponse.setMensaje("Rol no autorizado para crear prácticas");
            practicaResponse.setDatos(null);
            return ResponseEntity.status(403).body(practicaResponse);
        }

        Practica practicaCreada = practicaService.crearPractica(
                request.getFechaini(),
                request.getFechafin(),
                request.getDescripcion(),
                request.getEmpresa(),
                request.getJefePRactica(),
                request.getEstudianteId(),
                request.getProfesorId());
                
        practicaResponse.setStatus(200);
        practicaResponse.setMensaje("Practica creada con exito");
        practicaResponse.setDatos(practicaCreada);

        return ResponseEntity.ok().body(practicaResponse);
    }

    // actualizar (profesores)
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Object> actualizarPractica(
            @PathVariable Long id,
            @RequestBody Practica datosPractica,
            @RequestHeader("USER-ID") Long userId,
            @RequestHeader("ROL") String role) {
        PracticaResponse practicaResponse = new PracticaResponse();

        if (!"PROFESOR".equalsIgnoreCase(role)) {
            practicaResponse.setStatus(403);
            practicaResponse.setMensaje("Solo los profesores pueden actualizar prácticas");
            practicaResponse.setDatos(null);
            return ResponseEntity.status(403).body(practicaResponse);
        }

        Practica practicaExistente = practicaService.obtenerPractica(id);
        if (practicaExistente == null) {
            practicaResponse.setStatus(404);
            practicaResponse.setMensaje("Práctica no encontrada");
            practicaResponse.setDatos(null);
            return ResponseEntity.ok().body(practicaResponse);
        }

        if (practicaExistente.getProfesor() == null || !userId.equals(practicaExistente.getProfesor().getId())) {
            practicaResponse.setStatus(403);
            practicaResponse.setMensaje("No tiene permiso para actualizar esta práctica");
            practicaResponse.setDatos(null);
            return ResponseEntity.status(403).body(practicaResponse);
        }

        Practica actualizada = practicaService.actualizarPractica(id, datosPractica);
        practicaResponse.setStatus(200);
        practicaResponse.setMensaje("Practica actualizada con exito");
        practicaResponse.setDatos(actualizada);
        return ResponseEntity.ok().body(practicaResponse);
    }

    // eliminar (profesores)
    @DeleteMapping(value = "eliminar/{id}", produces = "application/json")
    public ResponseEntity<Object> eliminarPractica(
            @PathVariable Long id,
            @RequestHeader("USER-ID") Long userId,
            @RequestHeader("ROL") String role) {
        PracticaResponse practicaResponse = new PracticaResponse();

        if (!"PROFESOR".equalsIgnoreCase(role)) {
            practicaResponse.setStatus(403);
            practicaResponse.setMensaje("Solo los profesores pueden eliminar prácticas");
            practicaResponse.setDatos(null);
            return ResponseEntity.status(403).body(practicaResponse);
        }

        Practica practicaExistente = practicaService.obtenerPractica(id);
        if (practicaExistente == null) {
            practicaResponse.setStatus(404);
            practicaResponse.setMensaje("Practica no encontrada");
            practicaResponse.setDatos(null);

            return ResponseEntity.ok().body(practicaResponse);
        }

        if (practicaExistente.getProfesor() == null || !userId.equals(practicaExistente.getProfesor().getId())) {
            practicaResponse.setStatus(403);
            practicaResponse.setMensaje("No tiene permiso para eliminar esta práctica");
            practicaResponse.setDatos(null);
            return ResponseEntity.status(403).body(practicaResponse);
        }
        practicaExistente.setActivo(false);
        practicaService.guardar(practicaExistente);

        practicaResponse.setStatus(200);
        practicaResponse.setMensaje("Practica eliminada con exito");
        practicaResponse.setDatos(null);

        return ResponseEntity.ok().body(practicaResponse);
    }

}
