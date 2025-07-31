package pe.gob.pj.rapidemanda.domain.utils;

public class ProjectConstants {

	public static final String METHOD_CORTA_ULTIMA_BARRA_INVERTIDA = "PUT";
	
	public static final int DEFAULT_PAGINATION_PAGE_SIZE = 10;
	
	public class Esquema {
		public static final String RAPIDEMANDA = "rapidemanda";
		public static final String AUDITORIA_GENERAL = "auditoriageneral";
	}

	public class Alfresco {
		public static final String FOLDER_SINOE = "NEGOCIO";
		public static final String VERSION_4_1 = "4.1";
		public static final String VERSION_4_2 = "4.2";
	}
	
	public class Aplicativo {
		public static final String NOMBRE = "rapidemanda-api";
		public static final String VERSION = "1.0.0";
	}
	
	public class Caracter {
		public static final String VACIO = "";
	}

	public class Pattern {
		public static final String SEXO = "[mfMF]";
		public static final String CELULAR = "\\+?([0-9])+";
		public static final String NUMBER = "\\d+";
		public static final String ALPHANUMBER = "[a-zA-Z0-9]+";
		public static final String ALPHA = "^[\\p{L}\\p{M}]+$";
		public static final String ALPHA_SPACIO = "^[\\p{L}\\p{M}]+( [\\p{L}\\p{M}]+)*$";
		public static final String ALPHANUMBER_COMAS = "[a-zA-Z0-9]+(,[a-zA-Z0-9]+)*";
		public static final String S_N = "[SN]";
		public static final String FLAG_10 = "[10]";
		public static final String FORMATO_EXPEDIENTE = "(\\d{5})[-](\\d{4})[-](\\d{1,4})[-](\\d{4})[-]([A-Za-z]{2})[-]([A-Za-z]{2})[-](\\d{2})";
		public static final String FECHA = "([0][1-9]|[1-2][0-9]|[3][0-1])/([0][1-9]|[1][0-2])/(\\d{4})";
		public static final String FECHA_DD_MM_YYYY_HH_MM_SS_SSS = "(\\d{2})/(\\d{2})/(\\d{4}) (\\d{2}):(\\d{2}):(\\d{2})\\.(\\d{3})";
		public static final String FECHA_YYYY_MM_DD_HH_MM_SS_SSS = "(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2}):(\\d{2})\\.(\\d{3})";
		public static final String EMAIL = "([A-Za-z0-9]+[._-]?[A-Za-z0-9]+)+@([A-Za-z0-9]+[-]?[A-Za-z0-9]+\\.)*[A-Za-z0-9]{2,6}";
		public static final String IP = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
		public static final String MAC = "([0-9A-F]{2}[:-]){5}([0-9A-F]{2})";
		public static final String DECIMAL = "^\\d{1,15}(\\.\\d{1,2})?$";
	}

	public class Formato {
		public static final String FECHA_YYYYMMDD = "yyyyMMdd";
		public static final String FECHA_YYYY_MM_DD = "yyyy/MM/dd";
		public static final String FECHA_YYYY_MM_DD_ = "yyyy-MM-dd";
		public static final String FECHA_DD_MM_YYYY = "dd/MM/yyyy";
		public static final String FECHA_DD_MM_YYYY_HH_MM = "dd/MM/yyyy hh:mm a";
		public static final String FECHA_DD_MM_YYYY_HH_MM_SS = "dd/MM/yyyy hh:mm:ss";
		public static final String FECHA_DD_MM_YYYY_HH_MM_SS_SSS="dd/MM/yyyy HH:mm:ss.SSS";
		public static final String FECHA_YYYY_MM_DD_HH_MM_SS_SSS="yyyy-MM-dd HH:mm:ss.SSS";
	}
	
	public static final String AUD_CUO = "auditoriaCuo";
	public static final String AUD_IP = "auditoriaIp";
	public static final String AUD_IPS = "auditoriaIps";
	public static final String AUD_USUARIO = "auditoriaUsuario";
	public static final String AUD_URI = "auditoriaUri";
	public static final String AUD_PARAMS = "auditoriaParametrosUrl";
	public static final String AUD_HERRAMIENTA = "auditoriaAgente";
	public static final String AUD_JWT = "auditoriaJwt";
	
	public class Mensajes {
		public static final String MSG_ERROR_GENERICO_CONVERSION = "El tipo de dato de entrada es incorrecto";
	}

}
