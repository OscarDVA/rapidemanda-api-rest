package pe.gob.pj.rapidemanda.usecase;


import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.pj.rapidemanda.domain.enums.Errors;
import pe.gob.pj.rapidemanda.domain.exceptions.ErrorException;
import pe.gob.pj.rapidemanda.domain.model.auditoriageneral.AuditoriaAplicativos;
import pe.gob.pj.rapidemanda.domain.port.persistence.AuditoriaGeneralPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.AuditoriaGeneralUseCasePort;

@Service("auditoriaGeneralUseCasePort")
public class AuditoriaGeneralUseCaseAdapter implements AuditoriaGeneralUseCasePort{
	
	final AuditoriaGeneralPersistencePort auditoriaGeneralPersistencePort;
	
	public AuditoriaGeneralUseCaseAdapter(@Qualifier("auditoriaGeneralPersistencePort") AuditoriaGeneralPersistencePort auditoriaGeneralRepositoryPort) {
		this.auditoriaGeneralPersistencePort = auditoriaGeneralRepositoryPort;
	}

	@Async("poolTask")
	@Override
	@Transactional(transactionManager = "txManagerAuditoriaGeneral", propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = { Exception.class, SQLException.class})
	public void crear(String cuo, AuditoriaAplicativos auditoriaAplicativos)  throws Exception{
		try {
			this.auditoriaGeneralPersistencePort.crear(auditoriaAplicativos);
		} catch (Exception e) {
			throw new ErrorException(Errors.ERROR_AUDITORIA_GENERAL_REGISTRAR.getCodigo(), 
					Errors.ERROR_AUDITORIA_GENERAL_REGISTRAR.getNombre(), 
					e);
		}
	}

}
