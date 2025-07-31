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
@Table(name = "MOV_DEMANDADO", schema = ProjectConstants.Esquema.RAPIDEMANDA)
@NamedQuery(name = MovDemandado.Q_ALL, query = "SELECT mdd FROM MovDemandado mdd")
public class MovDemandado extends AuditoriaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String Q_ALL = "MovDemandado.q.all";
	
	@Id
	@SequenceGenerator(name = "SEQ_MOV_DEMANDADO", schema = ProjectConstants.Esquema.RAPIDEMANDA, sequenceName = "USEQ_MOV_DEMANDADO", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MOV_DEMANDADO")
	
	@Column(name = "N_DEMANDADO")
	private Integer id;

	@Column(name = "C_TIPO_DOCUMENTO", nullable = false)
	private String tipoDocumento;

	@Column(name = "X_NUM_DOCUMENTO", nullable = false)
	private String numeroDocumento;

	@Column(name = "X_RAZON_SOCIAL", nullable = false)
	private String razonSocial;

	@Column(name = "X_DEPARTAMENTO", nullable = false)
	private String departamento;

	@Column(name = "X_PROVINCIA", nullable = false)
	private String provincia;

	@Column(name = "X_DISTRITO", nullable = false)
	private String distrito;

	@Column(name = "C_TIPO_DOMICILIO", nullable = false)
	private String tipoDomicilio;

	@Column(name = "X_DOMICILIO", nullable = false)
	private String domicilio;

	@Column(name = "X_REFERENCIA", nullable = true)
	private String referencia;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "N_DEMANDA", nullable = false)
	private MovDemanda demanda;
}
