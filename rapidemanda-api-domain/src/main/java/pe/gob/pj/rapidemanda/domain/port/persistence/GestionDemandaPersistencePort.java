package pe.gob.pj.rapidemanda.domain.port.persistence;

import pe.gob.pj.rapidemanda.domain.model.servicio.Demanda;

import java.util.List;
import java.util.Map;

public interface GestionDemandaPersistencePort {

	/**
	 * Método que permite registrar una nueva demanda
	 * 
	 * @param cuo     Código único de operación
	 * @param demanda Modelo que contiene los datos de la demanda a registrar
	 * @return
	 * @throws Exception Si ocurre un error al registrar la demanda
	 */
	public void registrarDemanda(String cuo, Demanda demanda) throws Exception;

	/**
	 * Método que permite actualizar una demanda existente en estado BORRADOR
	 * 
	 * @param cuo     Código único de operación
	 * @param demanda Modelo que contiene los datos actualizados de la demanda
	 * @return
	 * @throws Exception Si la demanda no existe o no está en estado BORRADOR
	 */
	public void actualizarDemanda(String cuo, Demanda demanda) throws Exception;

	/**
	 * Método que permite buscar demandas según el CUO y filtros opcionales
	 * 
	 * @param cuo
	 * @param filters
	 * @return
	 * @throws Exception
	 */
	List<Demanda> buscarDemandas(String cuo, Map<String, Object> filters) throws Exception;
}
