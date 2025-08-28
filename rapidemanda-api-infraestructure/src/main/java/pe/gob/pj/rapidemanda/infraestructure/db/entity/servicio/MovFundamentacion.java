package pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.AuditoriaEntity;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "MOV_FUNDAMENTACION", schema = ProjectConstants.Esquema.RAPIDEMANDA)
@NamedQuery(name = MovFundamentacion.Q_ALL, query = "SELECT mf FROM MovFundamentacion mf")
public class MovFundamentacion extends AuditoriaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String Q_ALL = "MovFundamentacion.q.all";

	@Id
	@SequenceGenerator(name = "SEQ_MOV_FUNDAMENTACION", schema = ProjectConstants.Esquema.RAPIDEMANDA, sequenceName = "USEQ_MOV_FUNDAMENTACION", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MOV_FUNDAMENTACION")
	@Column(name = "N_FUNDAMENTACION")
	private Integer id;

	@Column(name = "X_CONTENIDO", nullable = false)
	private String xContenido;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "N_DEMANDA", nullable = false)
	private MovDemanda demanda;
}