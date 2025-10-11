package pe.gob.pj.rapidemanda.domain.port.persistence;

import java.util.List;

import pe.gob.pj.rapidemanda.domain.model.servicio.*;

public interface GestionDashboardPersistencePort {

    DashboardResumen obtenerResumen(String cuo) throws Exception;

    List<DemandaResumen> listarDemandasRecientes(String cuo, int limite) throws Exception;

    List<ConteoPetitorioSimilitudItem> contarPetitorios(String cuo) throws Exception;

    DemandanteConteos contarDemandantesSexoEdad(String cuo) throws Exception;

    List<ConteoParItem> contarDemandaPorTipoPresentacionYEstado(String cuo) throws Exception;

    List<ConteoItem> contarDemandaPorTipoPresentacion(String cuo) throws Exception;
}