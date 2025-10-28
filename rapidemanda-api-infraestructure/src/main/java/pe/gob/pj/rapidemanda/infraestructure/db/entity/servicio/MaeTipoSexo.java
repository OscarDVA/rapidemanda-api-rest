package pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio;

import java.io.Serializable;

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
@Entity
@Table(name = "MAE_TIPO_SEXO", schema = ProjectConstants.Esquema.RAPIDEMANDA)
@NamedQuery(name = MaeTipoSexo.Q_ALL, query = "SELECT mts FROM MaeTipoSexo mts")
public class MaeTipoSexo extends AuditoriaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String Q_ALL = "MaeTipoSexo.q.all";

	@Id
	@Column(name = "B_TIPO_SEXO", nullable = false)
	private String id;

	@Column(name = "X_NOMBRE", nullable = false)
	private String nombre;

	@Column(name = "X_ABREVIATURA", nullable = true)
	private String abreviatura;
}