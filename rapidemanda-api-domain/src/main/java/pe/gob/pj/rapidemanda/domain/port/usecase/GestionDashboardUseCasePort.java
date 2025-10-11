package pe.gob.pj.rapidemanda.domain.port.usecase;

import java.util.List;

import pe.gob.pj.rapidemanda.domain.model.servicio.DashboardGraficos;
import pe.gob.pj.rapidemanda.domain.model.servicio.DashboardResumen;
import pe.gob.pj.rapidemanda.domain.model.servicio.DemandaResumen;
import pe.gob.pj.rapidemanda.domain.model.servicio.ConteoPetitorioSimilitudItem;
import pe.gob.pj.rapidemanda.domain.model.servicio.DemandanteConteos;

public interface GestionDashboardUseCasePort {

    DashboardResumen obtenerResumen(String cuo) throws Exception;


    DashboardGraficos obtenerGraficos(String cuo) throws Exception;

    List<DemandaResumen> obtenerDemandasRecientes(String cuo, Integer limite) throws Exception;

    List<ConteoPetitorioSimilitudItem> obtenerPetitorioConteos(String cuo) throws Exception;

    DemandanteConteos obtenerDemandanteConteos(String cuo) throws Exception;
}