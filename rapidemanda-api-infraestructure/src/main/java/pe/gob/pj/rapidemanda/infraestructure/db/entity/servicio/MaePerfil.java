package pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.AuditoriaEntity;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name="MAE_PERFIL", schema = ProjectConstants.Esquema.RAPIDEMANDA)
@NamedQueries(value= {
		@NamedQuery(name=MaePerfil.Q_ALL, query = "SELECT mp FROM MaePerfil mp")
})
@FilterDefs(value= {
		@FilterDef(name=MaePerfil.F_ID, parameters = {@ParamDef(name=MaePerfil.P_ID, type = Integer.class)}),
		@FilterDef(name=MaePerfil.F_ACTIVO, parameters = {@ParamDef(name=MaePerfil.P_ACTIVO, type = String.class)})
})
@Filters(value= {
		@Filter(name=MaePerfil.F_ID, condition = "N_PERFIL=:"+MaePerfil.P_ID),
		@Filter(name=MaePerfil.F_ACTIVO, condition = "L_ACTIVO=:"+MaePerfil.P_ACTIVO)
})
public class MaePerfil extends AuditoriaEntity implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String Q_ALL = "MaePerfil.q.all";
	
	public static final String F_ID = "MaePerfil.f.id";
	public static final String F_ACTIVO = "MaePerfil.f.activo";
	
	public static final String P_ID = "idMaePerfil";
	public static final String P_ACTIVO = "activoMaePerfil";

	@Id
	@SequenceGenerator(name="SEQ_MAE_PERFIL", schema = ProjectConstants.Esquema.RAPIDEMANDA, sequenceName = "USEQ_MAE_PERFIL" , initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MAE_PERFIL")
	@Column(name="N_PERFIL", nullable = false)
	private Integer id;
	@Column(name="X_NOMBRE", nullable = false)
	private String nombre;
	@Column(name="C_ROL", nullable = false)
	private String rol;
	
	@OneToMany(mappedBy = "perfil", fetch = FetchType.LAZY)
	private List<MovOpcionPerfil> perfilsOpcion = new ArrayList<MovOpcionPerfil>();
	
}
