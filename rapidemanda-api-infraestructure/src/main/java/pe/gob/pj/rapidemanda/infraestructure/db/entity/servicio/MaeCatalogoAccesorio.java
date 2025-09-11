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
@Table(name = "MAE_CATALOGO_ACCESORIO", schema = ProjectConstants.Esquema.RAPIDEMANDA)
@NamedQuery(name = MaeCatalogoAccesorio.Q_ALL, query = "SELECT mcp FROM MaeCatalogoAccesorio mcp")

@FilterDef(name = MaeCatalogoAccesorio.F_PRETENSION_PRINCIPAL_FILTER, parameters = @ParamDef(name = MaeCatalogoAccesorio.P_PRETENSION_PRINCIPAL_ID, type = Integer.class))
@FilterDef(name = MaeCatalogoAccesorio.F_CONCEPTO_FILTER, parameters = @ParamDef(name = MaeCatalogoAccesorio.P_CONCEPTO_ID, type = Integer.class))

@Filter(name = MaeCatalogoAccesorio.F_PRETENSION_PRINCIPAL_FILTER, condition = "(N_PRETENSION = :"
		+ MaeCatalogoAccesorio.P_PRETENSION_PRINCIPAL_ID + " OR (N_PRETENSION IS NULL AND :"
		+ MaeCatalogoAccesorio.P_PRETENSION_PRINCIPAL_ID + " = -1))")
@Filter(name = MaeCatalogoAccesorio.F_CONCEPTO_FILTER, condition = "(N_CONCEPTO = :"
		+ MaeCatalogoAccesorio.P_CONCEPTO_ID + " OR (N_CONCEPTO IS NULL AND :"
		+ MaeCatalogoAccesorio.P_CONCEPTO_ID + " = -1))")

public class MaeCatalogoAccesorio extends AuditoriaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String Q_ALL = "MaeCatalogoAccesorio.q.all";
	
	public static final String F_PRETENSION_PRINCIPAL_FILTER = "MaeCatalogoAccesorio.f.pretensionPrincipalFilter";
	public static final String F_CONCEPTO_FILTER = "MaeCatalogoAccesorio.f.conceptoFilter";
	
	public static final String P_PRETENSION_PRINCIPAL_ID = "pretensionPrincipalId";
	public static final String P_CONCEPTO_ID = "conceptoId";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "N_ACCESORIO", nullable = false)
	private Integer id;

	@Column(name = "X_NOMBRE", nullable = false)
	private String nombre;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "N_CONCEPTO", nullable = true)
	private MaeCatalogoConcepto concepto;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "N_PRETENSION", nullable = true)
	private MaeCatalogoPretension pretension;
}
