package pe.gob.pj.rapidemanda.domain.model.servicio;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class DemandaResumen implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String sumilla;
    private String tipoRecepcion;
    private String estado;
    private String tipoPresentacion;
    private String fechaRegistro;
    private String fechaRecepcion;
    private List<Demandante> demandantes;
    private List<Demandado> demandados;
}