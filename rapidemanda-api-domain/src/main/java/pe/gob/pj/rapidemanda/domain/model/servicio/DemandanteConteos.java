package pe.gob.pj.rapidemanda.domain.model.servicio;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class DemandanteConteos implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<ConteoItem> porSexo;
    private List<ConteoEdadSexoItem> porEdadRango;
}