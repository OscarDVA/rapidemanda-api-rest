package pe.gob.pj.rapidemanda.domain.model.servicio;

import java.io.Serializable;
import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Demanda implements Serializable {
	static final long serialVersionUID = 1L;
	
	public static final String P_ID = "idDemanda";
	public static final String P_ESTADO_ID = "idEstadoDemanda";
	public static final String P_USUARIO = "usuarioDemanda";

	Integer id;
	String sumilla;
	String idEstadoDemanda; // 'B', 'C', 'P'
	String estadoDemanda; // 'BORRADOR', 'COMPLETADO', 'PRESENTADO'
	String idTipoPresentacion; // 'M', 'F'
	String tipoPresentacion; // 'MPE', 'FISICA'
	Integer idUsuario;
	String usuarioDemanda;
	String pdfUrl;
	String activo;

	List<Demandante> demandantes;
	List<Demandado> demandados;
	List<Petitorio> petitorios;
	RelacionLaboral relacionLaboral;
	List<Fundamentacion> fundamentaciones;
	// List<Anexo> anexos;
	List<Firma> firmas;
}
