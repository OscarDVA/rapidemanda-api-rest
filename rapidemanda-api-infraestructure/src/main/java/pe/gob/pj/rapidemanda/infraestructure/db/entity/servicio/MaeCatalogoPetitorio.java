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
@Table(name = "MAE_CATALOGO_PETITORIO", schema = ProjectConstants.Esquema.RAPIDEMANDA)
@NamedQuery(name = MaeCatalogoPetitorio.Q_ALL, query = "SELECT mcp FROM MaeCatalogoPetitorio mcp")
public class MaeCatalogoPetitorio extends AuditoriaEntity implements Serializable {
	
    private static final long serialVersionUID = 1L;
    
    public static final String Q_ALL = "MaeCatalogoPetitorio.q.all";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "N_PETITORIO", nullable = false)
    private Integer id;
    
    @Column(name = "X_NOMBRE", nullable = false)
    private String nombre;
    
}
