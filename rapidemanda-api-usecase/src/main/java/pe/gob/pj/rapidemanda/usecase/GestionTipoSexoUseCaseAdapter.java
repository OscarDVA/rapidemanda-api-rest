package pe.gob.pj.rapidemanda.usecase;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.pj.rapidemanda.domain.enums.Errors;
import pe.gob.pj.rapidemanda.domain.enums.Proceso;
import pe.gob.pj.rapidemanda.domain.exceptions.ErrorException;
import pe.gob.pj.rapidemanda.domain.model.servicio.TipoSexo;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionTipoSexoPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionTipoSexoUseCasePort;

import java.sql.SQLException;
import java.util.List;

@Service("gestionTipoSexoUseCasePort")
public class GestionTipoSexoUseCaseAdapter implements GestionTipoSexoUseCasePort {

    private final GestionTipoSexoPersistencePort gestionTipoSexoPersistencePort;

    public GestionTipoSexoUseCaseAdapter(
            @Qualifier("gestionTipoSexoPersistencePort") GestionTipoSexoPersistencePort gestionTipoSexoPersistencePort) {
        this.gestionTipoSexoPersistencePort = gestionTipoSexoPersistencePort;
    }

    @Override
    @Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = true, rollbackFor = {
            Exception.class, SQLException.class })
    public List<TipoSexo> buscarTiposSexo(String cuo) throws Exception {
        List<TipoSexo> lista = gestionTipoSexoPersistencePort.buscarTiposSexo(cuo);

        if (lista.isEmpty()) {
            throw new ErrorException(Errors.DATOS_NO_ENCONTRADOS.getCodigo(),
                    String.format(Errors.DATOS_NO_ENCONTRADOS.getNombre(), Proceso.TIPO_SEXO_CONSULTAR.getNombre()));
        }

        return lista;
    }
}