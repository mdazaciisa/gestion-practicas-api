package com.evaluacion.practicapp.responses;

import com.evaluacion.practicapp.models.Practica;
import lombok.Data;

@Data
public class PracticaResponse {
    private int status;
    private String mensaje;
    private Practica datos;

}
