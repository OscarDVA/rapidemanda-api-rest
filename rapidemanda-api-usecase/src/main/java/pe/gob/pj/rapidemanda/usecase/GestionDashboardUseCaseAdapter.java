package pe.gob.pj.rapidemanda.usecase;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.pj.rapidemanda.domain.model.servicio.DashboardGraficos;
import pe.gob.pj.rapidemanda.domain.model.servicio.DashboardResumen;
import pe.gob.pj.rapidemanda.domain.model.servicio.DemandaResumen;
import pe.gob.pj.rapidemanda.domain.model.servicio.ConteoPetitorioSimilitudItem;
import pe.gob.pj.rapidemanda.domain.model.servicio.DemandanteConteos;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionDashboardPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionDashboardUseCasePort;

@Service("gestionDashboardUseCasePort")
public class GestionDashboardUseCaseAdapter implements GestionDashboardUseCasePort {

    private final GestionDashboardPersistencePort gestionDashboardPersistencePort;

    public GestionDashboardUseCaseAdapter(
            @Qualifier("gestionDashboardPersistencePort") GestionDashboardPersistencePort gestionDashboardPersistencePort) {
        this.gestionDashboardPersistencePort = gestionDashboardPersistencePort;
    }

    @Override
    @Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = true, rollbackFor = {
            Exception.class, SQLException.class })
    public DashboardResumen obtenerResumen(String cuo) throws Exception {
        return gestionDashboardPersistencePort.obtenerResumen(cuo);
    }


    @Override
    @Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = true, rollbackFor = {
            Exception.class, SQLException.class })
    public DashboardGraficos obtenerGraficos(String cuo) throws Exception {
        DashboardGraficos graficos = new DashboardGraficos();
        graficos.setBarrasDemandaPorEstadoYTipo(gestionDashboardPersistencePort.contarDemandaPorTipoPresentacionYEstado(cuo));
        graficos.setTortaTipoPresentacion(gestionDashboardPersistencePort.contarDemandaPorTipoPresentacion(cuo));
        return graficos;
    }

    @Override
    @Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = true, rollbackFor = {
            Exception.class, SQLException.class })
    public List<DemandaResumen> obtenerDemandasRecientes(String cuo, Integer limite) throws Exception {
        return gestionDashboardPersistencePort.listarDemandasRecientes(cuo, limite != null ? limite : 5);
    }

    @Override
    @Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = true, rollbackFor = {
            Exception.class, SQLException.class })
    public List<ConteoPetitorioSimilitudItem> obtenerPetitorioConteos(String cuo) throws Exception {
        return gestionDashboardPersistencePort.contarPetitorios(cuo);
    }

    @Override
    @Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = true, rollbackFor = {
            Exception.class, SQLException.class })
    public DemandanteConteos obtenerDemandanteConteos(String cuo) throws Exception {
        return gestionDashboardPersistencePort.contarDemandantesSexoEdad(cuo);
    }
}