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
@Table(name = "MAE_TIPO_ANEXO", schema = ProjectConstants.Esquema.RAPIDEMANDA)
@NamedQuery(name = MaeTipoAnexo.Q_ALL, query = "SELECT mta FROM MaeTipoAnexo mta")
public class MaeTipoAnexo extends AuditoriaEntity implements Serializable {
	
    private static final long serialVersionUID = 1L;
    
    public static final String Q_ALL = "MaeTipoAnexo.q.all";
    
	@Id
	@Column(name = "B_TIPO_ANEXO")
	private String id;
	
	@Column(name = "X_NOMBRE", nullable = false)
	private String nombre;
		
}
