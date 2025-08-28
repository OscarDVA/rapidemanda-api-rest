package pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio;

import java.io.Serializable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.AuditoriaEntity;

@Data
@EqualsAndHashCode(callSuper = false)
@Table(name="MOV_USUARIO_PERFIL", schema = ProjectConstants.Esquema.RAPIDEMANDA)
@Entity
public class MovUsuarioPerfil extends AuditoriaEntity implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="SEQ_MOV_USUARIO_PERFIL", schema = ProjectConstants.Esquema.RAPIDEMANDA, sequenceName = "USEQ_MOV_USUARIO_PERFIL" , initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MOV_USUARIO_PERFIL")
	@Column(name="N_USUARIO_PERFIL", nullable = false)
	private Integer id;
	
	@ManyToOne(optional = false, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	@JoinColumn(name="N_USUARIO")
	private MovUsuario usuario;
	
	@ManyToOne(optional = false, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	@JoinColumn(name="N_PERFIL")
	private MaePerfil perfil;
	
	@Column(name = "L_ACTIVO", nullable = false)
    private String activo;
	
}
