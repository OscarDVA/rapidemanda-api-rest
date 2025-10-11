package pe.gob.pj.rapidemanda.domain.model.servicio;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class DashboardGraficos implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<ConteoParItem> barrasDemandaPorEstadoYTipo;
    private List<ConteoItem> tortaTipoPresentacion;
}