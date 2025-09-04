package pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio;

import java.io.Serializable;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.AuditoriaEntity;

@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "MAE_PROVINCIA", schema = ProjectConstants.Esquema.RAPIDEMANDA)
@Entity
@NamedQuery(name = MaeProvincia.Q_ALL, query = "SELECT mp FROM MaeProvincia mp")
@FilterDef(name = MaeProvincia.F_DEPARTAMENTO_FILTER, parameters = @ParamDef(name = MaeProvincia.P_DEPARTAMENTO_ID, type = String.class))
@FilterDef(name = MaeProvincia.F_PROVINCIA_FILTER, parameters = @ParamDef(name = MaeProvincia.P_PROVINCIA_ID, type = String.class))
@Filter(name = MaeProvincia.F_DEPARTAMENTO_FILTER, condition = "N_DEPARTAMENTO = :" + MaeProvincia.P_DEPARTAMENTO_ID)
@Filter(name = MaeProvincia.F_PROVINCIA_FILTER, condition = "N_PROVINCIA = :" + MaeProvincia.P_PROVINCIA_ID)

public class MaeProvincia extends AuditoriaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String Q_ALL = "MaeProvincia.q.all";
	public static final String F_DEPARTAMENTO_FILTER = "MaeProvincia.f.departamentoFilter";
	public static final String F_PROVINCIA_FILTER = "MaeProvincia.f.provinciaFilter";
	public static final String P_DEPARTAMENTO_ID = "departamentoId";
	public static final String P_PROVINCIA_ID = "provinciaId";

	@Id
	@Column(name = "N_PROVINCIA", nullable = false, length = 4)
	private String id;

	@Column(name = "X_NOMBRE", nullable = false)
	private String nombre;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "N_DEPARTAMENTO", nullable = false)
	private MaeDepartamento departamento;
}
