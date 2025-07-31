package pe.gob.pj.rapidemanda.domain.port.usecase;

import pe.gob.pj.rapidemanda.domain.model.auditoriageneral.AuditoriaAplicativos;

public interface AuditoriaGeneralUseCasePort {
	public void crear(String cuo, AuditoriaAplicativos auditoriaAplicativos) throws Exception;
}
