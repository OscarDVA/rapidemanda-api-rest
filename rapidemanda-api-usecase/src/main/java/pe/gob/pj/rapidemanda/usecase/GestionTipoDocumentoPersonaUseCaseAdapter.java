package pe.gob.pj.rapidemanda.usecase;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.pj.rapidemanda.domain.enums.Errors;
import pe.gob.pj.rapidemanda.domain.enums.Proceso;
import pe.gob.pj.rapidemanda.domain.exceptions.ErrorException;
import pe.gob.pj.rapidemanda.domain.model.servicio.TipoDocumentoPersona;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionTipoDocumentoPersonaPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionTipoDocumentoPersonaUseCasePort;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service("gestionTipoDocumentoPersonaUseCasePort")
public class GestionTipoDocumentoPersonaUseCaseAdapter implements GestionTipoDocumentoPersonaUseCasePort {

    private final GestionTipoDocumentoPersonaPersistencePort gestionTipoDocumentoPersonaPersistencePort;

    public GestionTipoDocumentoPersonaUseCaseAdapter(
            @Qualifier("gestionTipoDocumentoPersonaPersistencePort") GestionTipoDocumentoPersonaPersistencePort gestionTipoDocumentoPersonaPersistencePort) {
        this.gestionTipoDocumentoPersonaPersistencePort = gestionTipoDocumentoPersonaPersistencePort;
    }

    @Override
    @Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = true, rollbackFor = {
            Exception.class, SQLException.class })
    public List<TipoDocumentoPersona> buscarTiposDocumentoPersona(String cuo, Map<String, Object> filters) throws Exception {
        List<TipoDocumentoPersona> lista = gestionTipoDocumentoPersonaPersistencePort
                .buscarTiposDocumentoPersona(cuo, filters);

        if (lista.isEmpty()) {
            throw new ErrorException(Errors.DATOS_NO_ENCONTRADOS.getCodigo(), String
                    .format(Errors.DATOS_NO_ENCONTRADOS.getNombre(), Proceso.PERSONA_CONSULTAR.getNombre()));
        }
        return lista;
    }
}