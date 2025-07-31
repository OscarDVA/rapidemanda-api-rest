package pe.gob.pj.rapidemanda.domain.model.servicio;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RelacionLaboral implements Serializable {
	
	static final long serialVersionUID = 1L;
	
    private Integer id;
    private String regimen;
    private String fechaInicio;
    private String fechaFin;
    private Integer anios;
    private Integer meses;
    private Integer dias;
    private BigDecimal remuneracion;
    private Integer nDemanda;
    private String activo;
}
