package pe.gob.pj.rapidemanda.domain.port.persistence;

import java.util.Date;

public interface RecuperacionClavePersistencePort {

    /**
     * Crea una solicitud de recuperación de clave con token.
     *
     * @param cuo Código único de operación
     * @param usuario Nombre de usuario asociado
     * @param tokenHash Hash del token
     * @param expiraEn Fecha de expiración del token
     * @throws Exception
     */
    void crearSolicitud(String cuo, String usuario, String tokenHash, Date expiraEn) throws Exception;

    /**
     * Obtiene el usuario asociado a un token activo (no usado y no expirado).
     *
     * @param cuo Código único de operación
     * @param tokenHash Hash del token
     * @return Nombre de usuario o null si no existe
     * @throws Exception
     */
    String obtenerUsuarioPorTokenActivo(String cuo, String tokenHash) throws Exception;

    /**
     * Marca un token como utilizado.
     *
     * @param cuo Código único de operación
     * @param tokenHash Hash del token
     * @throws Exception
     */
    void marcarUsado(String cuo, String tokenHash) throws Exception;
}