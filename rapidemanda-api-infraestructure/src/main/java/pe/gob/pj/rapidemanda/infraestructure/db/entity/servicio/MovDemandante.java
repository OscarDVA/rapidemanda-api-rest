package pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.AuditoriaEntity;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "MOV_DEMANDANTE", schema = ProjectConstants.Esquema.RAPIDEMANDA)
@NamedQuery(name = MovDemandante.Q_ALL, query = "SELECT mdt FROM MovDemandante mdt")
public class MovDemandante extends AuditoriaEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String Q_ALL = "MovDemandante.q.all";
	
	@Id
	@SequenceGenerator(name="SEQ_MOV_DEMANDANTE", schema = ProjectConstants.Esquema.RAPIDEMANDA, sequenceName = "USEQ_MOV_DEMANDANTE" , initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MOV_DEMANDANTE")
    @Column(name = "N_DEMANDANTE")
    private Integer id;
    
    @Column(name = "C_TIPO_DOCUMENTO", nullable = false)
    private String tipoDocumento;
    
    @Column(name = "X_NUM_DOCUMENTO", nullable = false)
    private String numeroDocumento;
    
    @Column(name = "X_RAZON_SOCIAL", nullable = false)
    private String razonSocial;
    
    @Column(name = "C_GENERO")
    private String genero;
    
    @Column(name = "F_NACIMIENTO" )
    private Date fechaNacimiento;
    
    @Column(name = "X_DEPARTAMENTO")
    private String departamento;
    
    @Column(name = "X_PROVINCIA")
    private String provincia;
    
    @Column(name = "X_DISTRITO")
    private String distrito;
    
    @Column(name = "C_TIPO_DOMICILIO")
    private String tipoDomicilio;
    
    @Column(name = "X_DOMICILIO")
    private String domicilio;
    
    @Column(name = "X_REFERENCIA")
    private String referencia;
    
    @Column(name = "X_CORREO")
    private String correo;
    
    @Column(name = "X_CELULAR")
    private String celular;
    
    @Column(name = "X_CASILLA_ELECTRONICA")
    private String casillaElectronica;
    
    @Column(name = "L_APODERADO_COMUN", nullable = false, length = 1)
    private String apoderadoComun;
    
    @Column(name = " X_ARCHIVO_URL")
    private String archivoUrl;
    
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "N_DEMANDA", nullable = false)
	private MovDemanda demanda;
	
}
