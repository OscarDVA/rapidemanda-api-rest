package pe.gob.pj.rapidemanda.domain.model.servicio;

import lombok.Data;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Demandado implements Serializable{
	
	static final long serialVersionUID = 1L;

	 Integer id;
     String tipoDocumento;
     String numeroDocumento;
     String razonSocial;
     String departamento;
     String provincia;
     String distrito;
     String tipoDomicilio;
     String domicilio;
     String referencia;
     Integer nDemanda;
     String activo;
}
