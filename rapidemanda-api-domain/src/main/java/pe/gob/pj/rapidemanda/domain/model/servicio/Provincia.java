package pe.gob.pj.rapidemanda.domain.model.servicio;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Provincia implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String P_DEPARTAMENTO_ID = "departamentoId";
	public static final String P_PROVINCIA_ID = "provinciaId";

	private String id;
	private String nombre;
	private String departamentoId;
	private String activo;
}
