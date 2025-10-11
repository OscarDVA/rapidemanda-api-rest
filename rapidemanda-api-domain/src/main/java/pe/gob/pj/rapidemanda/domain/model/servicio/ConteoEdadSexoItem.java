package pe.gob.pj.rapidemanda.domain.model.servicio;

import java.io.Serializable;

import lombok.Data;

@Data
public class ConteoEdadSexoItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String edad; // rango: "18-65" o "65+"
    private Long varon;  // conteo M
    private Long mujer;  // conteo F
    private Long total;  // varon + mujer
}