package pe.gob.pj.rapidemanda.domain.model.servicio;

import java.io.Serializable;

import lombok.Data;

@Data
public class ConteoPetitorioSimilitudItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String tipo;
    private String pretensionPrincipal;
    private String concepto;
    private String pretensionAccesoria;
    private long total;
}