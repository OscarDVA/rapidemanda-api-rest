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
import java.util.Date;
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
        @FilterDef(name = MovDemanda.F_ESTADO_DEMANDA_MULTI, parameters = {
                @ParamDef(name = MovDemanda.P_ESTADO_DEMANDA_LIST, type = String.class) }),
        @FilterDef(name = MovDemanda.F_USUARIO, parameters = {
                @ParamDef(name = MovDemanda.P_USUARIO, type = Integer.class) }),
        @FilterDef(name = MovDemanda.F_TIPO_PRESENTACION, parameters = {
                @ParamDef(name = MovDemanda.P_TIPO_PRESENTACION, type = String.class) }),
        @FilterDef(name = MovDemanda.F_TIPO_RECEPCION, parameters = {
                @ParamDef(name = MovDemanda.P_TIPO_RECEPCION, type = String.class) }),
        @FilterDef(name = MovDemanda.F_USUARIO_RECEPCION, parameters = {
                @ParamDef(name = MovDemanda.P_USUARIO_RECEPCION, type = Integer.class) }),
        @FilterDef(name = MovDemanda.F_FECHA_COMPLETADO_RANGO, parameters = {
                @ParamDef(name = MovDemanda.P_FECHA_COMPLETADO_INICIO, type = Date.class),
                @ParamDef(name = MovDemanda.P_FECHA_COMPLETADO_FIN, type = Date.class)
        })
})

@Filters(value = {
        @Filter(name = MovDemanda.F_ID, condition = "N_DEMANDA=:" + MovDemanda.P_ID),
        @Filter(name = MovDemanda.F_ESTADO_DEMANDA, condition = "B_ESTADO_DEMANDA=:" + MovDemanda.P_ESTADO_DEMANDA),
        @Filter(name = MovDemanda.F_ESTADO_DEMANDA_MULTI, condition = "B_ESTADO_DEMANDA in (:" + MovDemanda.P_ESTADO_DEMANDA_LIST + ")"),
        @Filter(name = MovDemanda.F_USUARIO, condition = "N_USUARIO=:" + MovDemanda.P_USUARIO),
        @Filter(name = MovDemanda.F_TIPO_PRESENTACION, condition = "B_TIPO_PRESENTACION=:" + MovDemanda.P_TIPO_PRESENTACION),
        @Filter(name = MovDemanda.F_TIPO_RECEPCION, condition = "C_TIPO_RECEPCION=:" + MovDemanda.P_TIPO_RECEPCION),
        @Filter(name = MovDemanda.F_USUARIO_RECEPCION, condition = "N_USUARIO_RECEPCION=:" + MovDemanda.P_USUARIO_RECEPCION),
        @Filter(name = MovDemanda.F_FECHA_COMPLETADO_RANGO, condition = "F_COMPLETADO >= :" + MovDemanda.P_FECHA_COMPLETADO_INICIO + " and F_COMPLETADO <= :" + MovDemanda.P_FECHA_COMPLETADO_FIN)
})

public class MovDemanda extends AuditoriaEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String Q_ALL = "MovDemanda.q.all";

	public static final String F_ID = "MovDemanda.f.idDemanda";
    public static final String F_ESTADO_DEMANDA = "MovDemanda.f.idEstadoDemanda";
    public static final String F_ESTADO_DEMANDA_MULTI = "MovDemanda.f.idEstadoDemandaMulti";
    public static final String F_USUARIO = "MovDemanda.f.usuario";
    public static final String F_TIPO_PRESENTACION = "MovDemanda.f.tipoPresentacion";
    public static final String F_TIPO_RECEPCION = "MovDemanda.f.tipoRecepcion";
    public static final String F_USUARIO_RECEPCION = "MovDemanda.f.usuarioRecepcion";
    public static final String F_FECHA_COMPLETADO_RANGO = "MovDemanda.f.fechaCompletadoRango";

	public static final String P_ID = "idMovDemanda";
    public static final String P_ESTADO_DEMANDA = "idestadoDemandaMovDemanda";
    public static final String P_ESTADO_DEMANDA_LIST = "idestadoDemandaMovDemandaList";
    public static final String P_USUARIO = "usuarioMovDemanda";
    public static final String P_TIPO_PRESENTACION = "idTipoPresentacionMovDemanda";
    public static final String P_TIPO_RECEPCION = "tipoRecepcionMovDemanda";
    public static final String P_USUARIO_RECEPCION = "idUsuarioRecepcionMovDemanda";
    public static final String P_FECHA_COMPLETADO_INICIO = "fechaCompletadoInicioMovDemanda";
    public static final String P_FECHA_COMPLETADO_FIN = "fechaCompletadoFinMovDemanda";

	@Id
	@SequenceGenerator(name = "SEQ_MOV_DEMANDA", schema = ProjectConstants.Esquema.RAPIDEMANDA, sequenceName = "USEQ_MOV_DEMANDA", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MOV_DEMANDA")

	@Column(name = "N_DEMANDA", nullable = false)
	private Integer id;

	@Column(name = "X_SUMILLA")
	private String sumilla;

	@Column(name = "X_PDF_URL")
	private String pdfUrl;
	
	@Column(name = "C_TIPO_RECEPCION")
	private String tipoRecepcion;
	
	@Column(name = "F_RECEPCION")
	private Date fechaRecepcion;

	@Column(name = "F_COMPLETADO")
	private Date fechaCompletado;

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
    
//    @OneToMany(mappedBy = "demanda", cascade = CascadeType.ALL, orphanRemoval = true)
//    @BatchSize(size = 10)
//    private List<MovAnexo> anexos;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "N_USUARIO", referencedColumnName = "N_USUARIO", insertable = true, updatable = true)
	private MovUsuario usuarioDemanda;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "N_USUARIO_RECEPCION", referencedColumnName = "N_USUARIO", insertable = true, updatable = true)
	private MovUsuario usuarioRecepcion;
	

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "B_ESTADO_DEMANDA")
	private MaeEstadoDemanda estadoDemanda;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "B_TIPO_PRESENTACION")
	private MaeTipoPresentacion tipoPresentacion;
}
