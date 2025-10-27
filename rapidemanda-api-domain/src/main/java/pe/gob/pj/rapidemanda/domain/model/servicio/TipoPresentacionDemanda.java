package pe.gob.pj.rapidemanda.domain.model.servicio;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TipoPresentacionDemanda implements Serializable {

    static final long serialVersionUID = 1L;

    String id;
    String nombre;
    String activo;
}