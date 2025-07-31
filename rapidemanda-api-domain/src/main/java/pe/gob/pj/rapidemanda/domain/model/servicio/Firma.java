package pe.gob.pj.rapidemanda.domain.model.servicio;

import lombok.Data;

import java.io.Serializable;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Firma implements Serializable{
	
	static final long serialVersionUID = 1L;
	
	private Integer id;
    private String tipo;
    private String archivoUrl;
    private Integer nDemanda;
    private String activo;
}
