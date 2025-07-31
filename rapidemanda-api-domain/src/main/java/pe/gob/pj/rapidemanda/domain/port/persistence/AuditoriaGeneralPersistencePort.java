package pe.gob.pj.rapidemanda.domain.port.persistence;

import pe.gob.pj.rapidemanda.domain.model.auditoriageneral.AuditoriaAplicativos;

public interface AuditoriaGeneralPersistencePort {
	public void crear(AuditoriaAplicativos auditoriaAplicativos) throws Exception;
}
