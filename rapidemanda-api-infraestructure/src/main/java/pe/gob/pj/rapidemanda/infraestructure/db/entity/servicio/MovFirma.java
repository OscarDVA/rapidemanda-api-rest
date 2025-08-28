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
@Table(name = "MOV_FIRMA", schema = ProjectConstants.Esquema.RAPIDEMANDA)
@NamedQuery(name = MovFirma.Q_ALL, query = "SELECT mf FROM MovFirma mf")
public class MovFirma extends AuditoriaEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public static final String Q_ALL = "MovFirma.q.all";
	
	@Id
	@SequenceGenerator(name="SEQ_MOV_FIRMA", schema = ProjectConstants.Esquema.RAPIDEMANDA, sequenceName = "USEQ_MOV_FIRMA" , initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MOV_FIRMA")
	
    @Column(name = "N_FIRMA")
    private Integer id;
    
    @Column(name = "C_TIPO", nullable = false)
    private String cTipo;
    
    @Column(name = "X_ARCHIVO_URL")
    private String xArchivoUrl;
      
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "N_DEMANDA", nullable = false)
	private MovDemanda demanda;
}