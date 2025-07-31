package pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.AuditoriaEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false, exclude = {"demanda"})
@Table(name = "MOV_RELACION_LABORAL", schema = ProjectConstants.Esquema.RAPIDEMANDA)
@Entity
@NamedQuery(name = MovRelacionLaboral.Q_ALL, query = "SELECT mrl FROM MovRelacionLaboral mrl")
public class MovRelacionLaboral extends AuditoriaEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public static final String Q_ALL = "MovRelacionLaboral.q.all";
	
	@Id
	@SequenceGenerator(name="SEQ_MOV_RELACION_LABORAL", schema = ProjectConstants.Esquema.RAPIDEMANDA, sequenceName = "USEQ_MOV_RELACION_LABORAL" , initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MOV_RELACION_LABORAL")
	
    @Column(name = "N_RELACION")
    private Integer id;
    
    @Column(name = "C_REGIMEN")
    private String regimen;
    
    @Column(name = "F_INICIO")
    private Date fechaInicio;
    
    @Column(name = "F_FIN")
    private Date fechaFin;
    
    @Column(name = "N_ANIOS")
    private Integer anios;
    
    @Column(name = "N_MESES")
    private Integer meses;
    
    @Column(name = "N_DIAS")
    private Integer dias;
    
    @Column(name = " N_REMUNERACION")
    private BigDecimal remuneracion;
        
    @ToString.Exclude
    @OneToOne
    @JoinColumn(name = "N_DEMANDA",nullable = false)
    private MovDemanda demanda;
}
