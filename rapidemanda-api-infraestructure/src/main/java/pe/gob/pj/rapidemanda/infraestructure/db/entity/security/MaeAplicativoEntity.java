package pe.gob.pj.rapidemanda.infraestructure.db.entity.security;

import java.io.Serializable;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pe.gob.pj.rapidemanda.domain.utils.SecurityConstants;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.AuditoriaEntity;

import java.util.List;


/**
 * The persistent class for the mae_aplicativo database table.
 * 
 */
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name="mae_aplicativo", schema = SecurityConstants.ESQUEMA_SEGURIDAD)
@NamedQuery(name="MaeAplicativo.findAll", query="SELECT m FROM MaeAplicativoEntity m")
public class MaeAplicativoEntity extends AuditoriaEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
    @Id
	@Column(name="n_aplicativo")
	private Integer nAplicativo;

	@Column(name="c_aplicativo")
	private String cAplicativo;

	@Column(name="x_aplicativo")
	private String xAplicativo;

	@Column(name="x_descripcion")
	private String xDescripcion;

	//bi-directional many-to-one association to MaeTipoAplicativo
	@ManyToOne
	@JoinColumn(name="n_tipo_aplicativo")
	private MaeTipoAplicativoEntity maeTipoAplicativo;

	//bi-directional many-to-one association to MaeOperacion
	@OneToMany(mappedBy="maeAplicativo")
	private List<MaeOperacionEntity> maeOperacions;
	
}