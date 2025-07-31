package pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.AuditoriaEntity;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "MAE_CATALOGO_PRETENSION", schema = ProjectConstants.Esquema.RAPIDEMANDA)
@NamedQuery(name = MaeCatalogoPretension.Q_ALL, query = "SELECT mcp FROM MaeCatalogoPretension mcp")
public class MaeCatalogoPretension extends AuditoriaEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public static final String Q_ALL = "MaeCatalogoPretension.q.all";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "N_PRETENSION")
	private Integer id;

	@Column(name = "X_NOMBRE", nullable = false)
	private String nombre;

	@Column(name = "L_TIENE_CONCEPTOS", nullable = false, length = 1)
	private String tieneConceptos;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "N_PETITORIO", nullable = false)
	private MaeCatalogoPetitorio petitorio;

	@OneToMany(mappedBy = "pretension", fetch = FetchType.LAZY)
	private List<MaeCatalogoConcepto> conceptos;

	@OneToMany(mappedBy = "pretension", fetch = FetchType.LAZY)
	private List<MaeCatalogoAccesorio> accesorios;
}
