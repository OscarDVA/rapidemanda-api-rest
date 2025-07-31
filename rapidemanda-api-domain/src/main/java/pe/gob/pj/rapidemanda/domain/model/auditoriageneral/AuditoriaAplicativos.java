package pe.gob.pj.rapidemanda.domain.model.auditoriageneral;

import java.util.Date;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import pe.gob.pj.rapidemanda.domain.model.Auditoria;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@EqualsAndHashCode(callSuper = true)
public class AuditoriaAplicativos extends Auditoria{
	
	Long id;

	Date fechaRegistro;

	String uri;
	
	String peticionUrl;
	
	String peticionBody;
	
	String usuarioAplicativo;
	
	String usuarioAuth;
	
	String ips;
	
	String codigoUnicoOperacion;
	
	String codigoRespuesta;
	
	String descripcionRespuesta;
	
	Integer duracionRespuesta;
	
	String herramientaConsume;
	
}
