package pe.gob.pj.rapidemanda.infraestructure.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum Claim {

	ROLES("rols"),
	ROL_SELECCIONADO("rolSeleccionado"),
	USUARIO_REALIZA_PETICION("usuario"),
	IP_REALIZA_PETICION("remoteIp"),
	LIMITE_TOKEN("limit");
	
	String nombre;
	
	Claim(String nombre){
		this.nombre = nombre;
	}
	
}
