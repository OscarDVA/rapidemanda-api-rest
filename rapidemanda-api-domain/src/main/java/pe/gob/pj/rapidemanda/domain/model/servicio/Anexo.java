package pe.gob.pj.rapidemanda.domain.model.servicio;

import lombok.Data;

import java.io.Serializable;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Anexo implements Serializable{
	
	static final long serialVersionUID = 1L;
	
    Integer id;
    String tipo;
    String incluido;
    String archivoUrl;
    Integer nDemanda;
    String activo;
}
