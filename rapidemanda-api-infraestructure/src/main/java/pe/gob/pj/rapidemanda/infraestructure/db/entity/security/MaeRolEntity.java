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
 * The persistent class for the mae_rol database table.
 * 
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name="mae_rol", schema = SecurityConstants.ESQUEMA_SEGURIDAD)
@NamedQuery(name="MaeRol.findAll", query="SELECT m FROM MaeRolEntity m")
@NamedQuery(name=MaeRolEntity.FIND_ROLES_BY_ID_USUARIO, query="SELECT m FROM MaeRolEntity m JOIN m.maeRolUsuarios ur WHERE m.activo = '1' AND ur.maeUsuario.nUsuario = :idUsuario")
public class MaeRolEntity extends AuditoriaEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String FIND_ROLES_BY_ID_USUARIO = "MaeRol.rolesPorUsuario";

	@Id
	@Column(name="n_rol")
	private Integer nRol;

	@Column(name="c_rol")
	private String cRol;

	@Column(name="x_descripcion")
	private String xDescripcion;

	@Column(name="x_rol")
	private String xRol;

	//bi-directional many-to-one association to MaeOperacion
	@OneToMany(mappedBy="maeRol")
	private List<MaeOperacionEntity> maeOperacions;

	//bi-directional many-to-one association to MaeRolUsuario
	@OneToMany(mappedBy="maeRol")
	private List<MaeRolUsuarioEntity> maeRolUsuarios;

}