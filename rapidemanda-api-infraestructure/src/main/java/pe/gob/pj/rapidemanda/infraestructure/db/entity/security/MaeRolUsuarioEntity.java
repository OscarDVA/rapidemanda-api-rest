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
 * The persistent class for the mae_rol_usuario database table.
 * 
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name="mae_rol_usuario", schema = SecurityConstants.ESQUEMA_SEGURIDAD)
@NamedQuery(name="MaeRolUsuario.findAll", query="SELECT m FROM MaeRolUsuarioEntity m")
@NamedQuery(name= MaeRolUsuarioEntity.AUTENTICAR_USUARIO, query = "SELECT u FROM MaeRolUsuarioEntity ru "
		+ "JOIN ru.maeRol r "
		+ "JOIN r.maeOperacions op "
		+ "JOIN op.maeAplicativo ap "
		+ "JOIN ru.maeUsuario u "
		+ "JOIN u.maeCliente c "
		+ "WHERE ru.activo = '1' AND u.activo = '1' AND r.activo = '1' AND ap.activo = '1' AND c.activo = '1' "
		+ "AND u.cUsuario = :cUsuario AND r.cRol = :codRol AND ap.nAplicativo = :nAplicativo AND c.cCliente = :cCliente ")
@NamedQuery(name= MaeRolUsuarioEntity.VALIDAR_ACCESO_METODO, query="SELECT ru FROM MaeRolUsuarioEntity ru "
		+ "JOIN ru.maeRol r "
		+ "JOIN r.maeOperacions op "
		+ "JOIN ru.maeUsuario u "
		+ "WHERE u.activo = '1' AND ru.activo = '1' AND r.activo = '1' AND op.activo = '1' "
		+ "AND r.cRol = :codRol AND u.cUsuario = :cUsuario AND op.xEndpoint =:operacion " )
public class MaeRolUsuarioEntity extends AuditoriaEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String AUTENTICAR_USUARIO = "MaeAplicativoRolOperacion.autenticarUsuario";
    public static final String VALIDAR_ACCESO_METODO = "MaeAplicativoRolOperacion.validaAccesoMetodo";
	
	public static final String P_COD_USUARIO = "cUsuario";
	public static final String P_COD_APLICATIVO = "codAplicativo";
	public static final String P_COD_CLIENTE = "cCliente";
	public static final String P_N_APLICATIVO = "nAplicativo";
	public static final String P_COD_ROL = "codRol";
	public static final String P_OPERACION = "operacion";
	
	@Id
	@Column(name="n_rol_usuario")
	private Integer nRolUsuario;

	//bi-directional many-to-one association to MaeRol
	@ManyToOne
	@JoinColumn(name="n_rol")
	private MaeRolEntity maeRol;

	//bi-directional many-to-one association to MaeUsuario
	@ManyToOne
	@JoinColumn(name="n_usuario")
	private MaeUsuarioEntity maeUsuario;

}