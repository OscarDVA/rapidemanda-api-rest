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
@Table(name = "MAE_CATALOGO_ACCESORIO", schema = ProjectConstants.Esquema.RAPIDEMANDA)
@NamedQuery(name = MaeCatalogoAccesorio.Q_ALL, query = "SELECT mcp FROM MaeCatalogoAccesorio mcp")
public class MaeCatalogoAccesorio extends AuditoriaEntity implements Serializable{
	
    private static final long serialVersionUID = 1L;
    
    public static final String Q_ALL = "MaeCatalogoAccesorio.q.all";
	
	   @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "N_ACCESORIO")
	    private Integer id;
	    
	    @Column(name = "X_NOMBRE", nullable = false)
	    private String nombre;
	    
	    @Column(name = "L_ACTIVO", nullable = false, length = 1)
	    private String activo;
	    
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "N_CONCEPTO")
	    private MaeCatalogoConcepto concepto;
	    
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "N_PRETENSION")
	    private MaeCatalogoPretension pretension;
}
