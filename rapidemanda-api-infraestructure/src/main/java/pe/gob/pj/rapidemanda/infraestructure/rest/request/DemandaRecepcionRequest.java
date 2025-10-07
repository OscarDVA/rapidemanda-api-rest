package pe.gob.pj.rapidemanda.infraestructure.rest.request;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DemandaRecepcionRequest implements Serializable {
    static final long serialVersionUID = 1L;

    String nuevoEstadoDemanda; // debe ser 'P'
    String tipoRecepcion;      // 'VIRTUAL', 'PRESENCIAL'
    String fechaRecepcion;     // formato esperado dd/MM/yyyy
    Integer idUsuarioRecepcion;
}