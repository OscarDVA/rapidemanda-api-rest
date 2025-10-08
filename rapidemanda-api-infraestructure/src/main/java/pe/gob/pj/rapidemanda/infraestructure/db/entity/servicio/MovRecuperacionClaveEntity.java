package pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.AuditoriaEntity;

@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "mov_recuperacion_clave", schema = ProjectConstants.Esquema.RAPIDEMANDA)
@Entity

public class MovRecuperacionClaveEntity extends AuditoriaEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MOV_RECUPERACION_CLAVE")
    @SequenceGenerator(name = "SEQ_MOV_RECUPERACION_CLAVE", schema = ProjectConstants.Esquema.RAPIDEMANDA,
            sequenceName = "useq_mov_recuperacion_clave", initialValue = 1, allocationSize = 1)
    @Column(name = "N_RECUPERACION", nullable = false)
    private Integer id;

    @Column(name = "C_TOKEN_HASH", nullable = false, length = 512)
    private String tokenHash;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "F_EXPIRA", nullable = false)
    private Date fExpira;

    @Column(name = "L_USADO", length = 1, nullable = false)
    private String lUsado = "0"; // 0: no usado, 1: usado

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "F_USO")
    private Date fUso;
    
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "N_USUARIO", referencedColumnName = "N_USUARIO", insertable = true, updatable = true)
	private MovUsuario usuarioRecuperacion;
}