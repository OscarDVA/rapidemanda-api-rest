package pe.gob.pj.rapidemanda.usecase;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.pj.rapidemanda.domain.enums.Errors;
import pe.gob.pj.rapidemanda.domain.enums.Proceso;
import pe.gob.pj.rapidemanda.domain.exceptions.ErrorException;
import pe.gob.pj.rapidemanda.domain.model.servicio.TipoPresentacionDemanda;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionTipoPresentacionDemandaPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionTipoPresentacionDemandaUseCasePort;

import java.sql.SQLException;
import java.util.List;

@Service("gestionTipoPresentacionDemandaUseCasePort")
public class GestionTipoPresentacionDemandaUseCaseAdapter implements GestionTipoPresentacionDemandaUseCasePort {

    private final GestionTipoPresentacionDemandaPersistencePort gestionTipoPresentacionDemandaPersistencePort;

    public GestionTipoPresentacionDemandaUseCaseAdapter(
            @Qualifier("gestionTipoPresentacionDemandaPersistencePort") GestionTipoPresentacionDemandaPersistencePort gestionTipoPresentacionDemandaPersistencePort) {
        this.gestionTipoPresentacionDemandaPersistencePort = gestionTipoPresentacionDemandaPersistencePort;
    }

    @Override
    @Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = true, rollbackFor = {
            Exception.class, SQLException.class })
    public List<TipoPresentacionDemanda> buscarTiposPresentacionDemanda(String cuo) throws Exception {
        List<TipoPresentacionDemanda> lista = gestionTipoPresentacionDemandaPersistencePort.buscarTiposPresentacionDemanda(cuo);

        if (lista.isEmpty()) {
            throw new ErrorException(Errors.DATOS_NO_ENCONTRADOS.getCodigo(),
                    String.format(Errors.DATOS_NO_ENCONTRADOS.getNombre(), Proceso.TIPO_PRESENTACION_DEMANDA_CONSULTAR.getNombre()));
        }

        return lista;
    }
}