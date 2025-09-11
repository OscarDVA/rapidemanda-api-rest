package pe.gob.pj.rapidemanda.domain.model.servicio;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Persona implements Serializable{
	
	static final long serialVersionUID = 1L;
	
	public static final String P_NUMERO_DOCUMENTO = "numeroDocumentoPersona";
	public static final String P_CORREO = "correoPersona";
	
	Integer id;
	String numeroDocumento;
	String fechaNacimiento;
	String primerApellido;
	String segundoApellido;
	String nombres;
	String sexo;
	String correo;
	String telefono;
	String activo;
	
	String idTipoDocumento;
	String tipoDocumento;

}
