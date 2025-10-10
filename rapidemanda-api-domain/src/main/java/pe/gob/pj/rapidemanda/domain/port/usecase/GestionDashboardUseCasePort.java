package pe.gob.pj.rapidemanda.domain.port.usecase;

import pe.gob.pj.rapidemanda.domain.model.servicio.DashboardGraficos;
import pe.gob.pj.rapidemanda.domain.model.servicio.DashboardListas;
import pe.gob.pj.rapidemanda.domain.model.servicio.DashboardResumen;

public interface GestionDashboardUseCasePort {

    DashboardResumen obtenerResumen(String cuo) throws Exception;

    DashboardListas obtenerListas(String cuo, Integer limite) throws Exception;

    DashboardGraficos obtenerGraficos(String cuo) throws Exception;
}