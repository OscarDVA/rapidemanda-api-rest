package pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.AuditoriaEntity;

@Data
@EqualsAndHashCode(callSuper = false, exclude = {"demanda"})
@Entity
@Table(name = "MOV_ANEXO", schema = ProjectConstants.Esquema.RAPIDEMANDA)
@NamedQuery(name = MovAnexo.Q_ALL, query = "SELECT ma FROM MovAnexo ma")
public class MovAnexo extends AuditoriaEntity implements Serializable {
	
    private static final long serialVersionUID = 1L;
    
    public static final String Q_ALL = "MovAnexo.q.all";
    
	@Id
	@SequenceGenerator(name = "SEQ_MOV_ANEXO", schema = ProjectConstants.Esquema.RAPIDEMANDA, sequenceName = "USEQ_MOV_ANEXO", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MOV_ANEXO")
	@Column(name = "N_ANEXO")
	private Integer id;
	
	@Column(name = "C_TIPO", nullable = false)
	private String tipo;
	
	@Column(name = "L_INCLUIDO", nullable = false, length = 1)
	private String incluido;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "N_DEMANDA", nullable = false)
	private MovDemanda demanda;
}
