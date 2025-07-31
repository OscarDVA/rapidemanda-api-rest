package pe.gob.pj.rapidemanda.domain.model.servicio;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CatalogoPetitorio implements Serializable {

	static final long serialVersionUID = 1L;
	Integer id;
	String nombre;
	String activo;
	List<PretensionPrincipal> pretensionPrincipal;

	@Data
	public static class PretensionPrincipal implements Serializable {
		
		static final long serialVersionUID = 1L;
		Integer id;
		String nombre;
		String activo;
		String tieneConceptos;
		List<Concepto> conceptos;
		List<PretensionAccesoria> pretensionAccesoria;
	}

	@Data
	public static class Concepto implements Serializable {
		
		static final long serialVersionUID = 1L;
		Integer id;
		String nombre;
		String activo;
		List<PretensionAccesoria> pretensionAccesoria;
	}

	@Data
	public static class PretensionAccesoria implements Serializable {
		
		static final long serialVersionUID = 1L;
		Integer id;
		String nombre;
		String activo;
		Integer idRelacion;
	}
}