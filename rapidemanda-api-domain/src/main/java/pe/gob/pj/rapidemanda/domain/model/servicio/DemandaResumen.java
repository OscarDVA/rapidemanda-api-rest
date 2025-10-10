package pe.gob.pj.rapidemanda.domain.model.servicio;

import java.io.Serializable;

import lombok.Data;

@Data
public class DemandaResumen implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String estado;
    private String tipoPresentacion;
    private String fechaRegistro; 
}