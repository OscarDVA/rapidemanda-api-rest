package pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;

import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.AuditoriaEntity;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "MOV_DEMANDA", schema = ProjectConstants.Esquema.RAPIDEMANDA)
@Entity
@NamedQueries(value = {
		@NamedQuery(name = MovDemanda.Q_ALL, query = "SELECT md FROM MovDemanda md JOIN md.estadoDemanda JOIN md.tipoPresentacion JOIN md.usuarioDemanda") })

@FilterDefs(value = {
		@FilterDef(name = MovDemanda.F_ID, parameters = { @ParamDef(name = MovDemanda.P_ID, type = Integer.class) }),
		@FilterDef(name = MovDemanda.F_ESTADO_DEMANDA, parameters = {
				@ParamDef(name = MovDemanda.P_ESTADO_DEMANDA, type = String.class) }),
		@FilterDef(name = MovDemanda.F_USUARIO, parameters = {
				@ParamDef(name = MovDemanda.P_USUARIO, type = Integer.class) }) })

@Filters(value = { @Filter(name = MovDemanda.F_ID, condition = "N_DEMANDA=:" + MovDemanda.P_ID),
		@Filter(name = MovDemanda.F_ESTADO_DEMANDA, condition = "B_ESTADO_DEMANDA=:" + MovDemanda.P_ESTADO_DEMANDA),
		@Filter(name = MovDemanda.F_USUARIO, condition = "N_USUARIO=:" + MovDemanda.P_USUARIO) })

public class MovDemanda extends AuditoriaEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String Q_ALL = "MovDemanda.q.all";

	public static final String F_ID = "MovDemanda.f.idDemanda";
	public static final String F_ESTADO_DEMANDA = "MovDemanda.f.idEstadoDemanda";
	public static final String F_USUARIO = "MovDemanda.f.usuario";

	public static final String P_ID = "idMovDemanda";
	public static final String P_ESTADO_DEMANDA = "idestadoDemandaMovDemanda";
	public static final String P_USUARIO = "usuarioMovDemanda";

	@Id
	@SequenceGenerator(name = "SEQ_MOV_DEMANDA", schema = ProjectConstants.Esquema.RAPIDEMANDA, sequenceName = "USEQ_MOV_DEMANDA", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MOV_DEMANDA")

	@Column(name = "N_DEMANDA", nullable = false)
	private Integer id;

	@Column(name = "X_SUMILLA", nullable = false)
	private String sumilla;

	@Column(name = "X_PDF_URL")
	private String pdfUrl;

	@OneToMany(mappedBy = "demanda", cascade = CascadeType.ALL, orphanRemoval = true)
	@BatchSize(size = 10)
	private List<MovDemandante> demandantes;

	@OneToMany(mappedBy = "demanda", cascade = CascadeType.ALL, orphanRemoval = true)
	@BatchSize(size = 10)
	private List<MovDemandado> demandados;
    
    @OneToMany(mappedBy = "demanda", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<MovPetitorio> petitorios;
	
	@OneToOne(mappedBy = "demanda", cascade = CascadeType.ALL, orphanRemoval = true)
	private MovRelacionLaboral relacionLaboral;
    
    @OneToMany(mappedBy = "demanda", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<MovFundamentacion> fundamentaciones;
   
    @OneToMany(mappedBy = "demanda", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<MovFirma> firmas;
    
    @OneToMany(mappedBy = "demanda", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<MovAnexo> anexos;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "N_USUARIO", referencedColumnName = "N_USUARIO", insertable = true, updatable = true)
	private MovUsuario usuarioDemanda;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "B_ESTADO_DEMANDA")
	private MaeEstadoDemanda estadoDemanda;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "B_TIPO_PRESENTACION")
	private MaeTipoPresentacion tipoPresentacion;
}
