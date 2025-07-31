package pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio;

import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.AuditoriaEntity;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "MAE_TIPO_PRESENTACION", schema = ProjectConstants.Esquema.RAPIDEMANDA)
@NamedQueries(value = {
		@NamedQuery(name = MaeTipoPresentacion.Q_ALL, query = "SELECT mtp FROM MaeTipoPresentacion mtp") })
public class MaeTipoPresentacion extends AuditoriaEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String Q_ALL = "MaeTipoPresentacion.q.all";

	public static final String TABLA = "MAE_TIPO_PRESENTACION";
	public static final String COLUMNA_NOMBRE = "X_TIPO";

	@Id
	@Column(name = "B_TIPO_PRESENTACION", nullable = false, length = 1)
	private String bTipoPresentacion;

	@Column(name = "X_TIPO", nullable = false)
	private String xTipo;

}
