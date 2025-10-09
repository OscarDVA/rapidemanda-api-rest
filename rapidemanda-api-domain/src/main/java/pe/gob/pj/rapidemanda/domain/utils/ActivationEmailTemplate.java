package pe.gob.pj.rapidemanda.domain.utils;

import java.util.Date;

public class ActivationEmailTemplate {

    public static String buildHtml(String usuario,
                                   Date fechaRegistro,
                                   Date expiraEn,
                                   String activationUrl) {
        String fechaStr = ProjectUtils.convertDateToString(fechaRegistro, ProjectConstants.Formato.FECHA_DD_MM_YYYY_HH_MM);
        String expiraStr = ProjectUtils.convertDateToString(expiraEn, ProjectConstants.Formato.FECHA_DD_MM_YYYY_HH_MM);

        StringBuilder sb = new StringBuilder();
        sb.append("<!doctype html>")
          .append("<html lang=\"es\">")
          .append("<head>")
          .append("<meta charset=\"UTF-8\">")
          .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">")
          .append("<title>Activa tu cuenta</title>")
          .append("<style>")
          .append("body{margin:0;padding:0;background:#f5f7fb;font-family:Arial,Helvetica,sans-serif;color:#222}")
          .append(".container{max-width:600px;margin:0 auto;padding:24px}")
          .append(".card{background:#ffffff;border-radius:12px;overflow:hidden;box-shadow:0 2px 12px rgba(16,24,40,.08)}")
          .append(".header{background:#7A0C0C;color:#fff;padding:16px 24px;text-align:center}")
          .append(".brand{font-size:16px;font-weight:600;letter-spacing:.2px}")
          .append(".content{padding:24px}")
          .append("h1{margin:0 0 12px 0;font-size:20px;}")
          .append("p{margin:0 0 12px 0;line-height:1.5}")
          .append(".btn{display:inline-block;background:#0c7a2f;color:#fff !important;text-decoration:none;padding:12px 20px;border-radius:8px;font-weight:600}")
          .append(".btn:hover{background:#0a6527;color:#fff}")
          .append(".meta{margin-top:16px;padding:12px 16px;background:#f9fafb;border:1px solid #eaecf0;border-radius:8px;font-size:13px;color:#475467}")
          .append(".footer{padding:16px 24px;color:#667085;font-size:12px}")
          .append("@media (max-width: 480px){.container{padding:12px}.content{padding:16px}.btn{width:100%;text-align:center}}")
          .append("</style>")
          .append("</head>")
          .append("<body>")
          .append("<div class=\"container\">")
          .append("  <div class=\"card\">")
          .append("    <div class=\"header\">")
          .append("      <div class=\"brand\">Corte Superior de Justicia de Junín - RapiDemanda</div>")
          .append("    </div>")
          .append("    <div class=\"content\">")
          .append("      <h1>¡Bienvenido/a!</h1>")
          .append("      <p>Hola <strong>").append(escape(usuario)).append("</strong>, ¡Gracias por registrarte en RapiDemanda! Estamos encantados de tenerte como parte de nosotros.</p>")
          .append("      <p>Para activar tu cuenta, haz clic en el botón:</p>")
          .append("      <p style=\"margin:20px 0\"><a class=\"btn\" href=\"").append(escape(activationUrl)).append("\" target=\"_blank\">Activar cuenta</a></p>")
          .append("      <p>Si el botón no funciona, copia y pega este enlace en tu navegador:</p>")
          .append("      <p style=\"word-break:break-all;color:#344054\">").append(escape(activationUrl)).append("</p>")
          .append("      <div class=\"meta\">")
          .append("        <p><strong>Detalles del registro</strong></p>")
          .append("        <p>Fecha y hora: ").append(escape(fechaStr)).append("</p>")
          .append("        <p>Vigencia del enlace hasta: ").append(escape(expiraStr)).append("</p>")
          .append("      </div>")
          .append("      <p style=\"margin-top:16px;color:#475467\">Si no realizaste este registro, ignora este mensaje.</p>")
          .append("    </div>")
          .append("    <div class=\"footer\">Este correo fue enviado automáticamente por RAPIDEMANDA. No respondas a este mensaje.</div>")
          .append("  </div>")
          .append("</div>")
          .append("</body>")
          .append("</html>");
        return sb.toString();
    }

    public static String buildText(String usuario,
                                   Date fechaRegistro,
                                   Date expiraEn,
                                   String activationUrl) {
        String fechaStr = ProjectUtils.convertDateToString(fechaRegistro, ProjectConstants.Formato.FECHA_DD_MM_YYYY_HH_MM);
        String expiraStr = ProjectUtils.convertDateToString(expiraEn, ProjectConstants.Formato.FECHA_DD_MM_YYYY_HH_MM);
        StringBuilder sb = new StringBuilder();
        sb.append("RAPIDEMANDA - Corte Superior de Justicia de Junín\n")
          .append("-------------------------------------\n")
          .append("Activación de cuenta\n\n")
          .append("Hola ").append(nullSafe(usuario)).append(", gracias por registrarte.\n")
          .append("Para activar tu cuenta, abre este enlace: \n")
          .append(nullSafe(activationUrl)).append("\n\n")
          .append("Detalles del registro\n")
          .append("- Fecha y hora: ").append(nullSafe(fechaStr)).append("\n")
          .append("- Vigente hasta: ").append(nullSafe(expiraStr)).append("\n\n")
          .append("Si no realizaste este registro, ignora este mensaje.\n")
          .append("Este correo fue enviado automáticamente por RAPIDEMANDA.");
        return sb.toString();
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private static String nullSafe(String s) {
        return s == null ? "" : s;
    }
}