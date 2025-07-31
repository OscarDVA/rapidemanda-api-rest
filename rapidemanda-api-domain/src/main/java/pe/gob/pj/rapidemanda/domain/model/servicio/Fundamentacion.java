package pe.gob.pj.rapidemanda.domain.model.servicio;

import lombok.Data;

import java.io.Serializable;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Fundamentacion implements Serializable {
	
	static final long serialVersionUID = 1L;
	
    Integer id;
    String contenido;
    Integer nDemanda;
    String activo;
}
