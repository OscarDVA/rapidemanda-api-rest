package pe.gob.pj.rapidemanda.domain.model.client;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor @NoArgsConstructor
@Data
public class CaptchaValid implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 2449017822669485501L;
	
	private String success;
	private String challenge_ts;
	private String hostname;

}
