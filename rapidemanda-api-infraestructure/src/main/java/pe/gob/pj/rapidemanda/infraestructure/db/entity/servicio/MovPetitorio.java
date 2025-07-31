package pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.AuditoriaEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "MOV_PETITORIO", schema = ProjectConstants.Esquema.RAPIDEMANDA)
@NamedQuery(name = MovPetitorio.Q_ALL, query = "SELECT mp FROM MovPetitorio mp")
public class MovPetitorio extends AuditoriaEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public static final String Q_ALL = "MovPetitorio.q.all";

	@Id
	@SequenceGenerator(name="SEQ_MOV_PETITORIO", schema = ProjectConstants.Esquema.RAPIDEMANDA, sequenceName = "USEQ_MOV_PETITORIO" , initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MOV_PETITORIO")
    @Column(name = "N_PETITORIO")
    private Integer id;
    
    @Column(name = "C_TIPO", nullable = false)
    private String cTipo;
    
    @Column(name = "X_PRETENSION_PRINCIPAL", nullable = false)
    private String xPretensionPrincipal;
        
    @Column(name = "X_CONCEPTO", nullable = false)	
    private String xConcepto;
    
    @Column(name = "X_PRETENSION_ACCESORIA", nullable = false)
    private String xPretensionAccesoria;
        
    @Column(name = "N_MONTO", nullable = false)
    private BigDecimal nMonto;
    
    @Column(name = "X_JUSTIFICACION", nullable = false)
    private String xJustificacion;
    
    @Column(name = "F_INICIO", nullable = false)
    private Date fInicio;
    
    @Column(name = "F_FIN", nullable = false)
    private Date fFin;
    
    @Column(name = "N_DEMANDA", nullable = false, length = 1)
    private Integer nDemanda;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "N_DEMANDA", referencedColumnName = "N_DEMANDA", insertable = false, updatable = false)
    private MovDemanda demanda;
}
