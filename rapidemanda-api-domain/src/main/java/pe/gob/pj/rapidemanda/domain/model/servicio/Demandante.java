package pe.gob.pj.rapidemanda.domain.model.servicio;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Demandante implements Serializable {

	static final long serialVersionUID = 1L;

	Integer id;
	String tipoDocumento;
	String numeroDocumento;
	String razonSocial;
	String genero;
	String fechaNacimiento;
	String departamento;
	String provincia;
	String distrito;
	String tipoDomicilio;
	String domicilio;
	String referencia;
	String correo;
	String celular;
	String casillaElectronica;
	String apoderadoComun;
	String archivoUrl;
	Integer nDemanda;
	String activo;
}
