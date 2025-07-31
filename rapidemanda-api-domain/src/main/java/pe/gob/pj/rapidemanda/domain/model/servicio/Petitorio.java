package pe.gob.pj.rapidemanda.domain.model.servicio;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Petitorio implements Serializable{
	
	static final long serialVersionUID = 1L;
	
    Integer id;
    String tipo;
    String pretensionPrincipal;
    String concepto;
    String pretensionAccesoria;
    BigDecimal monto;
    String justificacion;
    String fechaInicio;
    String fechaFin;
    Integer nDemanda;
    String activo;
}
