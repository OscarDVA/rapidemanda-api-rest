package pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio;

import java.io.Serializable;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

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

@FilterDef(name = MaeCatalogoPretension.F_PETITORIO_FILTER, parameters = @ParamDef(name = MaeCatalogoPretension.P_PETITORIO_ID, type = Integer.class))
@Filter(name = MaeCatalogoPretension.F_PETITORIO_FILTER, condition = "N_PETITORIO = :" + MaeCatalogoPretension.P_PETITORIO_ID)

public class MaeCatalogoPretension extends AuditoriaEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public static final String Q_ALL = "MaeCatalogoPretension.q.all";
	public static final String F_PETITORIO_FILTER = "MaeCatalogoPretension.f.petitorioFilter";
	public static final String P_PETITORIO_ID = "petitorioId";

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

}
