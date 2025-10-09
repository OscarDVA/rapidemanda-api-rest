package pe.gob.pj.rapidemanda.domain.port.files;

public interface CorreoPort {

    /**
     * Envía un correo electrónico en formato HTML.
     *
     * @param cuo Código único de operación
     * @param para Dirección de correo destinatario
     * @param asunto Asunto del mensaje
     * @param cuerpoHtml Contenido del mensaje en HTML
     * @param cuerpoTexto Versión alternativa en texto plano
     * @throws Exception en caso de error al enviar
     */
    void enviar(String cuo, String para, String asunto, String cuerpoHtml, String cuerpoTexto) throws Exception;
}