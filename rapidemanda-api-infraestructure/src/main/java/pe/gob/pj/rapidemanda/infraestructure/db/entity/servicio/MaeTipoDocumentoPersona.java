package pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.AuditoriaEntity;

@Data
@EqualsAndHashCode(callSuper = false)
@Table(name="MAE_TIPO_DOCUMENTO_PERSONA", schema = ProjectConstants.Esquema.RAPIDEMANDA)
@NamedQueries(value = {
		@NamedQuery(name = MaeTipoDocumentoPersona.Q_ALL, query = "SELECT mtdp FROM MaeTipoDocumentoPersona mtdp")
})
@Entity
public class MaeTipoDocumentoPersona extends AuditoriaEntity implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String Q_ALL = "MaeTipoDocumentoPersona.q.all";

	public static final String TABLA = "MAE_TIPO_DOCUMENTO_PERSONA";
	public static final String COLUMNA_NOMBRE = "X_NOMBRE";	
	
	@Id
	@Column(name="B_TIPO_DOCUMENTO_PERSONA", nullable = false, length = 1)
	private String codigo;
	@Column(name="X_ABREVIATURA", nullable = false)
	private String abreviatura;

}
