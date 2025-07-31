package pe.gob.pj.rapidemanda.infraestructure.db.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;
import pe.gob.pj.rapidemanda.domain.utils.ProjectUtils;
import pe.gob.pj.rapidemanda.infraestructure.enums.Estado;
import pe.gob.pj.rapidemanda.infraestructure.enums.OperacionBaseDato;

@Getter @Setter
@MappedSuperclass
public abstract class AuditoriaEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="F_AUD")
	private Date fAud = new Date();
	@Column(name="B_AUD")
	private String bAud = OperacionBaseDato.INSERTAR.getNombre();
	@Column(name="C_AUD_UID")
	private String cAudId;
	@Column(name="C_AUD_UIDRED")
	private String cAudIdRed = ProjectUtils.getNombreRed();
	@Column(name="C_AUD_PC")
	private String cAudPc = ProjectUtils.getPc();
	@Column(name="C_AUD_IP")
	private String cAudIp = ProjectUtils.getIp();
	@Column(name="C_AUD_MCADDR")
	private String cAudMcAddr = ProjectUtils.getMac();
	
	@Column(name = "L_ACTIVO", length = 1, nullable = false)
	private String activo = Estado.ACTIVO_NUMERICO.getNombre();
//	@Temporal(TemporalType.TIMESTAMP)
//	@Column(name="F_REGISTRO", nullable = false)
//	private Date fechaRegistro = new Date();
}
