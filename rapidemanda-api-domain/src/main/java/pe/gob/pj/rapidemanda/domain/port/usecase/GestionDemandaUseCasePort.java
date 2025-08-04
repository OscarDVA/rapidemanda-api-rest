package pe.gob.pj.rapidemanda.domain.port.usecase;

import java.util.List;
import java.util.Map;

import pe.gob.pj.rapidemanda.domain.model.servicio.Demanda;

public interface GestionDemandaUseCasePort {

	/**
     * Método que permite registrar una nueva demanda
     * 
     * @param cuo Código único de operación
     * @param demanda Modelo que contiene los datos de la demanda a registrar
     * @return Demanda registrada con su ID generado
     * @throws Exception
     */
    public void registrarDemanda(String cuo, Demanda demanda) throws Exception;

    /**
     * Método que permite actualizar una demanda existente en estado BORRADOR
     * 
     * @param cuo Código único de operación
     * @param demanda Modelo que contiene los datos actualizados de la demanda
     * @return Demanda actualizada
     * @throws Exception Si la demanda no existe o no está en estado BORRADOR
     */
   public void actualizarDemanda(String cuo, Demanda demanda) throws Exception;

    /**
     * Método que permite buscar demandas según filtros
     * 
     * @param cuo Código único de operación
     * @param filters Mapa de filtros para la búsqueda
     * @return Lista de demandas que coinciden con los filtros
     * @throws Exception
     */
    public List<Demanda> buscarDemandas(String cuo, Map<String, Object> filters) throws Exception;
}
