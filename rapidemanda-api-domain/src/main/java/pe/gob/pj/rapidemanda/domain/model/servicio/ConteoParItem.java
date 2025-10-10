package pe.gob.pj.rapidemanda.domain.model.servicio;

import java.io.Serializable;

import lombok.Data;

@Data
public class ConteoParItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String clave1;
    private String clave2;
    private long total;
}