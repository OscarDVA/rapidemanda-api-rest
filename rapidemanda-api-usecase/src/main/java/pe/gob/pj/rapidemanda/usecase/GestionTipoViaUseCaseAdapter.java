package pe.gob.pj.rapidemanda.usecase;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.pj.rapidemanda.domain.enums.Errors;
import pe.gob.pj.rapidemanda.domain.enums.Proceso;
import pe.gob.pj.rapidemanda.domain.exceptions.ErrorException;
import pe.gob.pj.rapidemanda.domain.model.servicio.TipoVia;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionTipoViaPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionTipoViaUseCasePort;

import java.sql.SQLException;
import java.util.List;

@Service("gestionTipoViaUseCasePort")
public class GestionTipoViaUseCaseAdapter implements GestionTipoViaUseCasePort {

    private final GestionTipoViaPersistencePort gestionTipoViaPersistencePort;

    public GestionTipoViaUseCaseAdapter(
            @Qualifier("gestionTipoViaPersistencePort") GestionTipoViaPersistencePort gestionTipoViaPersistencePort) {
        this.gestionTipoViaPersistencePort = gestionTipoViaPersistencePort;
    }

    @Override
    @Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = true, rollbackFor = {
            Exception.class, SQLException.class })
    public List<TipoVia> buscarTiposVia(String cuo) throws Exception {
        List<TipoVia> lista = gestionTipoViaPersistencePort.buscarTiposVia(cuo);

        if (lista.isEmpty()) {
            throw new ErrorException(Errors.DATOS_NO_ENCONTRADOS.getCodigo(),
                    String.format(Errors.DATOS_NO_ENCONTRADOS.getNombre(), Proceso.TIPO_VIA_CONSULTAR.getNombre()));
        }

        return lista;
    }
}