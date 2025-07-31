package pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.AuditoriaEntity;

@Data
@EqualsAndHashCode(callSuper = false)
@Table(name="MOV_PERSONA", schema = ProjectConstants.Esquema.RAPIDEMANDA)
@Entity
@NamedQueries(value= {
		@NamedQuery(name=MovPersona.Q_ALL,query = "SELECT mp FROM MovPersona mp JOIN mp.tipoDocumento")
})
@FilterDefs(value= {
		@FilterDef(name=MovPersona.F_ID, parameters = {@ParamDef(name=MovPersona.P_ID, type=Integer.class)}),
		@FilterDef(name=MovPersona.F_TIPO_DOCUMENTO, parameters = {@ParamDef(name=MovPersona.P_TIPO_DOCUMENTO, type=String.class)}),
		@FilterDef(name=MovPersona.F_DOCUMENTO_IDENTIDAD, parameters = {@ParamDef(name=MovPersona.P_DOCUMENTO_IDENTIDAD, type=String.class)}),
		@FilterDef(name=MovPersona.F_PRIMER_APELLIDO, parameters = {@ParamDef(name=MovPersona.P_PRIMER_APELLIDO, type=String.class)}),
		@FilterDef(name=MovPersona.F_SEGUNDO_APELLIDO, parameters = {@ParamDef(name=MovPersona.P_SEGUNDO_APELLIDO, type=String.class)})
})
@Filters(value= {
		@Filter(name=MovPersona.F_ID, condition = "N_PERSONA=:"+MovPersona.P_ID),
		@Filter(name=MovPersona.F_TIPO_DOCUMENTO, condition = "B_TIPO_DOCUMENTO_PERSONA=:"+MovPersona.P_TIPO_DOCUMENTO),
		@Filter(name=MovPersona.F_DOCUMENTO_IDENTIDAD, condition = "X_DOCUMENTO_IDENTIDAD=:"+MovPersona.P_DOCUMENTO_IDENTIDAD),
		@Filter(name=MovPersona.F_PRIMER_APELLIDO, condition = "X_PRIMER_APELLIDO=:"+MovPersona.P_PRIMER_APELLIDO),
		@Filter(name=MovPersona.F_SEGUNDO_APELLIDO, condition = "X_SEGUNDO_APELLIDO=:"+MovPersona.P_SEGUNDO_APELLIDO)
})
public class MovPersona extends AuditoriaEntity implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String Q_ALL = "MovPersona.q.all";
	
	public static final String F_ID = "MovPersona.f.idPersona";
	public static final String F_TIPO_DOCUMENTO = "MovPersona.f.tipoDocumento";
	public static final String F_DOCUMENTO_IDENTIDAD = "MovPersona.f.documentoIdentidad";
	public static final String F_PRIMER_APELLIDO = "MovPersona.f.primerApellido";
	public static final String F_SEGUNDO_APELLIDO = "MovPersona.f.segundoApellido";

	public static final String P_ID = "idMovPersona";
	public static final String P_TIPO_DOCUMENTO = "tipoDocumentoMovPersona";
	public static final String P_DOCUMENTO_IDENTIDAD = "documentoIdentidadMovPersona";
	public static final String P_PRIMER_APELLIDO = "primerApellidoMovPersona";
	public static final String P_SEGUNDO_APELLIDO = "segundoApellidoMovPersona";
	
	@Id
	@SequenceGenerator(name="SEQ_MOV_PERSONA", schema = ProjectConstants.Esquema.RAPIDEMANDA, sequenceName = "USEQ_MOV_PERSONA" , initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MOV_PERSONA")
	@Column(name="N_PERSONA", nullable = false)
	private Integer id;
	@Column(name="X_DOCUMENTO_IDENTIDAD", nullable = false)
	private String numeroDocumento;
	@Column(name="F_NACIMIENTO", nullable = false)
	private Date fechaNacimiento;
	@Column(name="X_PRIMER_APELLIDO", nullable = false)
	private String primerApellido;
	@Column(name="X_SEGUNDO_APELLIDO", nullable = false)
	private String segundoApellido;
	@Column(name="X_NOMBRES", nullable = false)
	private String nombres;
	@Column(name="L_SEXO", nullable = false)
	private String sexo;
	@Column(name="X_CORREO", nullable = true)
	private String correo;
	@Column(name="X_TELEFONO", nullable = true)
	private String telefono;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	//@ManyToOne(optional = false, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	@JoinColumn(name="B_TIPO_DOCUMENTO_PERSONA")
	private MaeTipoDocumentoPersona tipoDocumento;
}
