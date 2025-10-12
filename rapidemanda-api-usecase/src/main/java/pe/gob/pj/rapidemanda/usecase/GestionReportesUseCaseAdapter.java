package pe.gob.pj.rapidemanda.usecase;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.pj.rapidemanda.domain.model.servicio.ConteoPetitorioTipoItem;
import pe.gob.pj.rapidemanda.domain.model.servicio.ConteoPetitorioTipoPretensionItem;
import pe.gob.pj.rapidemanda.domain.model.servicio.DemandanteConteos;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionReportesPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionReportesUseCasePort;

@Service("gestionReportesUseCasePort")
public class GestionReportesUseCaseAdapter implements GestionReportesUseCasePort {

    private final GestionReportesPersistencePort gestionReportesPersistencePort;

    public GestionReportesUseCaseAdapter(
            @Qualifier("gestionReportesPersistencePort") GestionReportesPersistencePort gestionReportesPersistencePort) {
        this.gestionReportesPersistencePort = gestionReportesPersistencePort;
    }

    @Override
    @Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = true, rollbackFor = { Exception.class, SQLException.class })
    public List<ConteoPetitorioTipoItem> obtenerConteosPetitorioPorTipo(String cuo, Date fechaInicio, Date fechaFin, List<String> estados) throws Exception {
        return gestionReportesPersistencePort.contarPetitoriosPorTipo(cuo, fechaInicio, fechaFin, estados);
    }

    @Override
    @Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = true, rollbackFor = { Exception.class, SQLException.class })
    public List<ConteoPetitorioTipoPretensionItem> obtenerConteosPetitorioPorTipoYPrincipal(String cuo, Date fechaInicio, Date fechaFin, List<String> estados) throws Exception {
        return gestionReportesPersistencePort.contarPetitoriosPorTipoYPrincipal(cuo, fechaInicio, fechaFin, estados);
    }

    @Override
    @Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = true, rollbackFor = { Exception.class, SQLException.class })
    public DemandanteConteos obtenerDemandanteConteosPorFechasEstados(String cuo, Date fechaInicio, Date fechaFin, List<String> estados) throws Exception {
        return gestionReportesPersistencePort.contarDemandantesSexoEdadPorFechasEstados(cuo, fechaInicio, fechaFin, estados);
    }
}