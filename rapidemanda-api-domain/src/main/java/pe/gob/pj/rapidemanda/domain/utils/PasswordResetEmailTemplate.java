package pe.gob.pj.rapidemanda.domain.utils;

import java.util.Date;

public class PasswordResetEmailTemplate {

    public static String buildHtml(String usuario,
                                   String ip,
                                   Date fechaSolicitud,
                                   Date expiraEn,
                                   String resetUrl) {
        String fechaStr = ProjectUtils.convertDateToString(fechaSolicitud, ProjectConstants.Formato.FECHA_DD_MM_YYYY_HH_MM);
        String expiraStr = ProjectUtils.convertDateToString(expiraEn, ProjectConstants.Formato.FECHA_DD_MM_YYYY_HH_MM);

        StringBuilder sb = new StringBuilder();
        sb.append("<!doctype html>")
          .append("<html lang=\"es\">")
          .append("<head>")
          .append("<meta charset=\"UTF-8\">")
          .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">")
          .append("<title>Restablecer contraseña</title>")
          .append("<style>")
          .append("body{margin:0;padding:0;background:#f5f7fb;font-family:Arial,Helvetica,sans-serif;color:#222}")
          .append(".container{max-width:600px;margin:0 auto;padding:24px}")
          .append(".card{background:#ffffff;border-radius:12px;overflow:hidden;box-shadow:0 2px 12px rgba(16,24,40,.08)}")
          .append(".header{background:#7A0C0C;color:#fff;padding:16px 24px;text-align:center}")
          .append(".brand{font-size:16px;font-weight:600;letter-spacing:.2px}")
          .append(".content{padding:24px}")
          .append("h1{margin:0 0 12px 0;font-size:20px;}")
          .append("p{margin:0 0 12px 0;line-height:1.5}")
          .append(".btn{display:inline-block;background:#7A0C0C;color:#fff !important;text-decoration:none;padding:12px 20px;border-radius:8px;font-weight:600}")
          .append(".btn:hover{background:#600A0A;color:#fff}")
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
          .append("      <h1>Restablecimiento de contraseña</h1>")
          .append("      <p>Hola <strong>").append(escape(usuario)).append("</strong>,</p>")
          .append("      <p>Recibimos una solicitud para restablecer la contraseña de tu cuenta. Para continuar, haz clic en el botón:</p>")
          .append("      <p style=\"margin:20px 0\"><a class=\"btn\" href=\"").append(escape(resetUrl)).append("\" target=\"_blank\">Restablecer contraseña</a></p>")
          .append("      <p>Si el botón no funciona, copia y pega este enlace en tu navegador:</p>")
          .append("      <p style=\"word-break:break-all;color:#344054\">").append(escape(resetUrl)).append("</p>")
          .append("      <div class=\"meta\">")
          .append("        <p><strong>Detalles de la solicitud</strong></p>")
          .append("        <p>Usuario: ").append(escape(usuario)).append("</p>")
          .append("        <p>IP de la solicitud: ").append(escape(ip)).append("</p>")
          .append("        <p>Fecha y hora: ").append(escape(fechaStr)).append("</p>")
          .append("        <p>Vigencia del enlace hasta: ").append(escape(expiraStr)).append("</p>")
          .append("      </div>")
          .append("      <p style=\"margin-top:16px;color:#475467\"><em>Si no solicitaste este cambio</em>, ignora este mensaje o avísanos para proteger tu cuenta.</p>")
          .append("    </div>")
          .append("    <div class=\"footer\">Este correo fue enviado automáticamente por RAPIDEMANDA. No respondas a este mensaje.</div>")
          .append("  </div>")
          .append("</div>")
          .append("</body>")
          .append("</html>");
        return sb.toString();
    }

    public static String buildText(String usuario,
                                   String ip,
                                   Date fechaSolicitud,
                                   Date expiraEn,
                                   String resetUrl) {
        String fechaStr = ProjectUtils.convertDateToString(fechaSolicitud, ProjectConstants.Formato.FECHA_DD_MM_YYYY_HH_MM);
        String expiraStr = ProjectUtils.convertDateToString(expiraEn, ProjectConstants.Formato.FECHA_DD_MM_YYYY_HH_MM);
        StringBuilder sb = new StringBuilder();
        sb.append("RAPIDEMANDA - Corte Superior de Justicia de Junín\n")
          .append("-------------------------------------\n")
          .append("Restablecimiento de contraseña\n\n")
          .append("Hola ").append(nullSafe(usuario)).append(",\n")
          .append("Recibimos una solicitud para restablecer la contraseña de tu cuenta.\n")
          .append("Para continuar, abre este enlace: \n")
          .append(nullSafe(resetUrl)).append("\n\n")
          .append("Detalles de la solicitud\n")
          .append("- Usuario: ").append(nullSafe(usuario)).append("\n")
          .append("- IP: ").append(nullSafe(ip)).append("\n")
          .append("- Fecha y hora: ").append(nullSafe(fechaStr)).append("\n")
          .append("- Vigente hasta: ").append(nullSafe(expiraStr)).append("\n\n")
          .append("Si no solicitaste este cambio, ignora este mensaje o avísanos para proteger tu cuenta.\n")
          .append("Este correo fue enviado automáticamente por RAPIDEMANDA.");
        return sb.toString();
    }

    private static String escape(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    private static String nullSafe(String v) {
        return v == null ? "" : v;
    }
}