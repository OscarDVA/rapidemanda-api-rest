package pe.gob.pj.rapidemanda.usecase;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.pj.rapidemanda.domain.enums.Errors;
import pe.gob.pj.rapidemanda.domain.enums.Proceso;
import pe.gob.pj.rapidemanda.domain.exceptions.ErrorException;
import pe.gob.pj.rapidemanda.domain.model.servicio.TipoRegimen;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionTipoRegimenPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionTipoRegimenUseCasePort;

import java.sql.SQLException;
import java.util.List;

@Service("gestionTipoRegimenUseCasePort")
public class GestionTipoRegimenUseCaseAdapter implements GestionTipoRegimenUseCasePort {

    private final GestionTipoRegimenPersistencePort gestionTipoRegimenPersistencePort;

    public GestionTipoRegimenUseCaseAdapter(
            @Qualifier("gestionTipoRegimenPersistencePort") GestionTipoRegimenPersistencePort gestionTipoRegimenPersistencePort) {
        this.gestionTipoRegimenPersistencePort = gestionTipoRegimenPersistencePort;
    }

    @Override
    @Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = true, rollbackFor = {
            Exception.class, SQLException.class })
    public List<TipoRegimen> buscarTiposRegimen(String cuo) throws Exception {
        List<TipoRegimen> lista = gestionTipoRegimenPersistencePort.buscarTiposRegimen(cuo);

        if (lista.isEmpty()) {
            throw new ErrorException(Errors.DATOS_NO_ENCONTRADOS.getCodigo(),
                    String.format(Errors.DATOS_NO_ENCONTRADOS.getNombre(), Proceso.TIPO_REGIMEN_CONSULTAR.getNombre()));
        }

        return lista;
    }
}