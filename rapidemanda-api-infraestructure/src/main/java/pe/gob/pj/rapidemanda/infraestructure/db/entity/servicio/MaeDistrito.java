package pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio;

import java.io.Serializable;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.AuditoriaEntity;

@Data
@EqualsAndHashCode(callSuper = false)
@Table(name="MAE_DISTRITO", schema = ProjectConstants.Esquema.RAPIDEMANDA)
@Entity
@NamedQuery(name = MaeDistrito.Q_ALL, query = "SELECT md FROM MaeDistrito md")
@FilterDef(name = MaeDistrito.F_PROVINCIA_FILTER, parameters = @ParamDef(name = MaeDistrito.P_PROVINCIA_ID, type = String.class))
@FilterDef(name = MaeDistrito.F_DISTRITO_FILTER, parameters = @ParamDef(name = MaeDistrito.P_DISTRITO_ID, type = String.class))
@Filter(name = MaeDistrito.F_PROVINCIA_FILTER, condition = "N_PROVINCIA = :" + MaeDistrito.P_PROVINCIA_ID)
@Filter(name = MaeDistrito.F_DISTRITO_FILTER, condition = "N_DISTRITO = :" + MaeDistrito.P_DISTRITO_ID)

public class MaeDistrito extends AuditoriaEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public static final String Q_ALL = "MaeDistrito.q.all";
	public static final String F_PROVINCIA_FILTER = "MaeDistrito.f.provinciaFilter";
	public static final String F_DISTRITO_FILTER = "MaeDistrito.f.distritoFilter";
	public static final String P_PROVINCIA_ID = "proviniciaId";
	public static final String P_DISTRITO_ID = "distritoId";
	
	@Id
    @Column(name = "N_DISTRITO",nullable = false, length = 6)
    private String id;

    @Column(name = "X_NOMBRE", nullable = false)
    private String nombre;  
    
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "N_PROVINCIA", nullable = false)
	private MaeProvincia provincia;
}
