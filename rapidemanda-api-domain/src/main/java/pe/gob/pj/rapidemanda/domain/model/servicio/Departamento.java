package pe.gob.pj.rapidemanda.domain.model.servicio;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Departamento implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public static final String P_DEPARTAMENTO_ID = "idDepartamento";
	
    private String id;
    private String nombre;
    private String activo;
}
