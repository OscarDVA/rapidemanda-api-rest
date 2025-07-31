package pe.gob.pj.rapidemanda.infraestructure.db.entity.security;

import java.io.Serializable;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pe.gob.pj.rapidemanda.domain.utils.SecurityConstants;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.AuditoriaEntity;



/**
 * The persistent class for the mae_operacion database table.
 * 
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name="mae_operacion", schema = SecurityConstants.ESQUEMA_SEGURIDAD)
@NamedQuery(name="MaeOperacion.findAll", query="SELECT m FROM MaeOperacionEntity m")
public class MaeOperacionEntity extends AuditoriaEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="n_operacion")
	private Integer nOperacion;

	@Column(name="x_descripcion")
	private String xDescripcion;

	@Column(name="x_endpoint")
	private String xEndpoint;

	@Column(name="x_operacion")
	private String xOperacion;

	//bi-directional many-to-one association to MaeAplicativo
	@ManyToOne
	@JoinColumn(name="n_aplicativo")
	private MaeAplicativoEntity maeAplicativo;

	//bi-directional many-to-one association to MaeRol
	@ManyToOne
	@JoinColumn(name="n_rol")
	private MaeRolEntity maeRol;

}