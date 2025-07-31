package pe.gob.pj.rapidemanda.domain.model.seguridad;

import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * @author Ramesh Fadatare
 *
 */
@NoArgsConstructor
@Data
@Entity
@Table(name="roles")
public class RoleSecurity
{
	@Id 
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column(nullable=false, unique=true)
	private String name;
		
	@ManyToMany(mappedBy="roles")
	private List<UserSecurity> users;

	public RoleSecurity(Integer id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
}
