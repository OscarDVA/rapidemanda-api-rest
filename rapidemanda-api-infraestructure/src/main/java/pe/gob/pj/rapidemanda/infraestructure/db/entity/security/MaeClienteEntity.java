package pe.gob.pj.rapidemanda.infraestructure.db.entity.security;

import java.io.Serializable;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pe.gob.pj.rapidemanda.domain.utils.SecurityConstants;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.AuditoriaEntity;

import java.util.List;


/**
 * The persistent class for the mae_cliente database table.
 * 
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name="mae_cliente", schema = SecurityConstants.ESQUEMA_SEGURIDAD)
@NamedQuery(name="MaeCliente.findAll", query="SELECT m FROM MaeClienteEntity m")
public class MaeClienteEntity extends AuditoriaEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="n_cliente")
	private Integer nCliente;

	@Column(name="c_cliente")
	private String cCliente;

	@Column(name="l_tipo_cliente")
	private String lTipoCliente;

	@Column(name="x_cliente")
	private String xCliente;

	@Column(name="x_descripcion")
	private String xDescripcion;

	//bi-directional many-to-one association to MaeUsuario
	@OneToMany(mappedBy="maeCliente")
	private List<MaeUsuarioEntity> maeUsuarios;

}