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
@Table(name = "MAE_TIPO_REGIMEN", schema = ProjectConstants.Esquema.RAPIDEMANDA)
@NamedQuery(name = MaeTipoRegimen.Q_ALL, query = "SELECT mtr FROM MaeTipoRegimen mtr")
public class MaeTipoRegimen extends AuditoriaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String Q_ALL = "MaeTipoRegimen.q.all";

	@Id
	@Column(name = "B_TIPO_REGIMEN", nullable = false)
	private String id;

	@Column(name = "X_NOMBRE", nullable = false)
	private String nombre;

	@Column(name = "X_ABREVIATURA", nullable = true)
	private String abreviatura;
}