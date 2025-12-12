package com.evaluacion.practicapp.responses;

import java.util.List;
import com.evaluacion.practicapp.models.Practica;
import lombok.Data;

@Data
public class PracticasResponse {
    private int status;
    private String mensaje;
    private List<Practica> datos;
}
