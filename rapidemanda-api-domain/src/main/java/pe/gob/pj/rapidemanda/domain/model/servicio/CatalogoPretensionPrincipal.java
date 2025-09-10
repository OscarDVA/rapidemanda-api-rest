package pe.gob.pj.rapidemanda.domain.model.servicio;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CatalogoPretensionPrincipal implements Serializable {

	static final long serialVersionUID = 1L;
	
	public static final String P_PETITORIO_ID = "petitorioId";
	public static final String P_TIENE_CONCEPTOS = "tieneConceptos";
	
	Integer id;
	String nombre;
	Integer petitorioId;
	String tieneConceptos;
	String activo;
}