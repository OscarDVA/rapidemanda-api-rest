package pe.gob.pj.rapidemanda.domain.model.servicio;

import java.io.Serializable;

import lombok.Data;

@Data
public class ConteoPetitorioTipoItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String tipo;
    private long total;
}