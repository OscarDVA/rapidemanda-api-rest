package pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio;

import java.io.Serializable;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.AuditoriaEntity;

@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "MAE_DEPARTAMENTO", schema = ProjectConstants.Esquema.RAPIDEMANDA)
@Entity
@NamedQuery(name = MaeDepartamento.Q_ALL, query = "SELECT mdep FROM MaeDepartamento mdep")
@FilterDefs(value= {
		@FilterDef(name = MaeDepartamento.F_ID, parameters = { @ParamDef(name = MaeDepartamento.P_ID, type = String.class) })})

@Filters(value= {
		@Filter(name=MaeDepartamento.F_ID, condition = "N_DEPARTAMENTO=:"+MaeDepartamento.P_ID)
})


public class MaeDepartamento extends AuditoriaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String Q_ALL = "MaeDepartamento.q.all";
	public static final String F_ID = "MaeDepartamento.f.idDepartamento";
	public static final String P_ID = "idMaeDepartamento";

	@Id
	@Column(name = "N_DEPARTAMENTO", nullable = false, length = 2)
	private String id;

	@Column(name = "X_NOMBRE", nullable = false)
	private String nombre;

}
