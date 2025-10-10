package pe.gob.pj.rapidemanda.domain.model.servicio;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class PetitorioConteos implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<ConteoItem> porTipo;
    private List<ConteoItem> porPretensionPrincipal;
    private List<ConteoItem> porConcepto;
    private List<ConteoItem> porPretensionAccesoria;
}