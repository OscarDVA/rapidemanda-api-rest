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
@Table(name = "MAE_CATALOGO_CONCEPTO", schema = ProjectConstants.Esquema.RAPIDEMANDA)
@NamedQuery(name = MaeCatalogoConcepto.Q_ALL, query = "SELECT mcc FROM MaeCatalogoConcepto mcc")
public class MaeCatalogoConcepto extends AuditoriaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String Q_ALL = "MaeCatalogoConcepto.q.all";
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "N_CONCEPTO")
	private Integer id;

	@Column(name = "X_NOMBRE", nullable = false)
	private String nombre;

	@Column(name = "L_ACTIVO", nullable = false, length = 1)
	private String activo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "N_PRETENSION", nullable = false)
	private MaeCatalogoPretension pretension;
}
