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
@Table(name = "MAE_CATALOGO_CONCEPTO", schema = ProjectConstants.Esquema.RAPIDEMANDA)
@NamedQuery(name = MaeCatalogoConcepto.Q_ALL, query = "SELECT mcc FROM MaeCatalogoConcepto mcc")
@FilterDef(name = MaeCatalogoConcepto.F_PRETENSION_PRINCIPAL_FILTER, parameters = @ParamDef(name = MaeCatalogoConcepto.P_PRETENSION_PRINCIPAL_ID, type = Integer.class))
@Filter(name = MaeCatalogoConcepto.F_PRETENSION_PRINCIPAL_FILTER, condition = "N_PRETENSION = :" + MaeCatalogoConcepto.P_PRETENSION_PRINCIPAL_ID)

public class MaeCatalogoConcepto extends AuditoriaEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final String Q_ALL = "MaeCatalogoConcepto.q.all";
	public static final String F_PRETENSION_PRINCIPAL_FILTER = "MaeCatalogoConcepto.f.pretensionPrincipalFilter";
	public static final String P_PRETENSION_PRINCIPAL_ID = "PretensionPrincipalId";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "N_CONCEPTO")
	private Integer id;

	@Column(name = "X_NOMBRE", nullable = false)
	private String nombre;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "N_PRETENSION", nullable = false)
	private MaeCatalogoPretension pretension;
}
