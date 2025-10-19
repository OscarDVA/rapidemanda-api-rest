package pe.gob.pj.rapidemanda.domain.model.servicio;
import lombok.Data;

import java.io.Serializable;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TipoAnexo implements Serializable {
	
	static final long serialVersionUID = 1L;
	
    String id;
    String nombre;
    String activo;
}
