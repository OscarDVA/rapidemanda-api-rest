package pe.gob.pj.rapidemanda.usecase;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.pj.rapidemanda.domain.model.seguridad.Rol;
import pe.gob.pj.rapidemanda.domain.model.seguridad.Usuario;
import pe.gob.pj.rapidemanda.domain.port.persistence.SeguridadPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.SeguridadUseCasePort;

@Service("seguridadUseCasePort")
public class SeguridadUseCaseAdapter implements SeguridadUseCasePort{

	final SeguridadPersistencePort seguridadPersistencePort;
	
	public SeguridadUseCaseAdapter(@Qualifier("seguridadPersistencePort") SeguridadPersistencePort seguridadPersistencePort) {
		this.seguridadPersistencePort = seguridadPersistencePort;
	}
	
	@Override
	@Transactional(transactionManager = "txManagerSeguridad", propagation = Propagation.REQUIRED, readOnly = true, rollbackFor = { Exception.class, SQLException.class})
	public String autenticarUsuario(String cuo, String codigoCliente, String codigoRol, String usuario, String clave)
			throws Exception {
		return seguridadPersistencePort.autenticarUsuario(cuo, codigoCliente, codigoRol, usuario, clave);
	}

	@Override
	@Transactional(transactionManager = "txManagerSeguridad", propagation = Propagation.REQUIRED, readOnly = true, rollbackFor = { Exception.class, SQLException.class})
	public Usuario recuperaInfoUsuario(String cuo, String id) throws Exception {
		return seguridadPersistencePort.recuperaInfoUsuario(cuo, id);
	}

	@Override
	@Transactional(transactionManager = "txManagerSeguridad", propagation = Propagation.REQUIRED, readOnly = true, rollbackFor = { Exception.class, SQLException.class})
	public List<Rol> recuperarRoles(String cuo, String id) throws Exception {
		return seguridadPersistencePort.recuperarRoles(cuo, id);
	}

	@Override
	@Transactional(transactionManager = "txManagerSeguridad", propagation = Propagation.REQUIRED, readOnly = true, rollbackFor = { Exception.class, SQLException.class})
	public String validarAccesoMetodo(String cuo, String usuario, String rol, String operacion) throws Exception {
		return seguridadPersistencePort.validarAccesoMetodo(cuo, usuario, rol, operacion);
	}

}
