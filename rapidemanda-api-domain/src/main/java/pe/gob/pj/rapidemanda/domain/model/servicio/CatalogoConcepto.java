package pe.gob.pj.rapidemanda.domain.model.servicio;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CatalogoConcepto implements Serializable {

	static final long serialVersionUID = 1L;

	public static final String P_PRETENSION_PRINCIPAL_ID = "pretensionPrincipalId";

	Integer id;
	String nombre;
	Integer pretensionPrincipalId;
	String activo;
}