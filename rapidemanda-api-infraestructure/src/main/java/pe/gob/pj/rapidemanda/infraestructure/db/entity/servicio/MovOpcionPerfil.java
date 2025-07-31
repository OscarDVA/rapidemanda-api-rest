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
@Table(name="MOV_OPCION_PERFIL", schema = ProjectConstants.Esquema.RAPIDEMANDA)
@Entity
public class MovOpcionPerfil extends AuditoriaEntity implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name="SEQ_MOV_OPCION_PERFIL", schema = ProjectConstants.Esquema.RAPIDEMANDA, sequenceName = "USEQ_MOV_OPCION_PERFIL" , initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MOV_OPCION_PERFIL")
	@Column(name="N_OPCION_PERFIL", nullable = false)
	private Integer id;
	@ManyToOne(optional = false, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	@JoinColumn(name="N_OPCION")
	private MaeOpcion opcion;
	@ManyToOne(optional = false, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	@JoinColumn(name="N_PERFIL")
	private MaePerfil perfil;

}
