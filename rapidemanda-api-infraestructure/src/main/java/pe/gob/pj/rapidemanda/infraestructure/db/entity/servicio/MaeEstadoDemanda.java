package pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio;

import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.AuditoriaEntity;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "MAE_ESTADO_DEMANDA", schema = ProjectConstants.Esquema.RAPIDEMANDA)
@NamedQueries(value = {
		@NamedQuery(name = MaeEstadoDemanda.Q_ALL, query = "SELECT med FROM MaeEstadoDemanda med")
})
public class MaeEstadoDemanda extends AuditoriaEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public static final String Q_ALL = "MaeEstadoDemanda.q.all";
	
	public static final String TABLA = "MAE_ESTADO_DEMANDA";
	public static final String COLUMNA_NOMBRE = "X_ESTADO";
	
	  @Id
	    @Column(name = "B_ESTADO_DEMANDA",  nullable = false, length = 1)
	    private String bEstadoDemanda;
	    
	    @Column(name = "X_ESTADO", nullable = false)
	    private String xEstado;

}
