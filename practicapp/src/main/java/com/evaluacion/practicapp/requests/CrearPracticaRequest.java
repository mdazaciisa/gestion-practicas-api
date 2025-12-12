package com.evaluacion.practicapp.requests;

import java.time.LocalDate;
import lombok.Data;

@Data
public class CrearPracticaRequest {
    private LocalDate fechaini;
    private LocalDate fechafin;
    private String descripcion;
    private String empresa;
    private String jefePRactica;
    private Long estudianteId;
    private Long profesorId;

}
