package pe.gob.pj.rapidemanda.infraestructure.db.entity.security;

import java.io.Serializable;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pe.gob.pj.rapidemanda.domain.utils.SecurityConstants;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.AuditoriaEntity;

import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the mae_usuario database table.
 * 
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name="mae_usuario", schema = SecurityConstants.ESQUEMA_SEGURIDAD)
@NamedQuery(name="MaeUsuario.findAll", query="SELECT m FROM MaeUsuarioEntity m")
@NamedQuery(name=MaeUsuarioEntity.FIND_BY_ID, query="SELECT m FROM MaeUsuarioEntity m WHERE m.activo = '1' AND m.nUsuario = :idUsuario ")
@NamedQuery(name=MaeUsuarioEntity.FIND_BY_CODIGO, query="SELECT m FROM MaeUsuarioEntity m WHERE m.activo = '1' AND m.cUsuario = :codigo ")
public class MaeUsuarioEntity extends AuditoriaEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String FIND_BY_ID = "MaeUsuario.buscarPorId";
	public static final String P_N_USUARIO = "idUsuario";
	public static final String FIND_BY_CODIGO = "MaeUsuario.buscarPorcodigo";
	public static final String P_N_CODIGO = "codigo";

	@Id
	@Column(name="n_usuario")
	private Integer nUsuario;

	@Column(name="c_clave")
	private String cClave;

	@Column(name="c_usuario")
	private String cUsuario;

	@Column(name="f_registro")
	private Timestamp fRegistro;

	//bi-directional many-to-one association to MaeRolUsuario
	@OneToMany(mappedBy="maeUsuario")
	private List<MaeRolUsuarioEntity> maeRolUsuarios;

	//bi-directional many-to-one association to MaeCliente
	@ManyToOne
	@JoinColumn(name="n_cliente")
	private MaeClienteEntity maeCliente;

}