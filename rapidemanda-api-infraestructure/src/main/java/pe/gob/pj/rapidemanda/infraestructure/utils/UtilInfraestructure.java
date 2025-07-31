package pe.gob.pj.rapidemanda.infraestructure.utils;

import lombok.extern.slf4j.Slf4j;
import pe.gob.pj.rapidemanda.infraestructure.enums.ErrorDefault;

@Slf4j
public class UtilInfraestructure {
	
	public static void handleException(String cuo, Exception e, String codigoError, String codigoMensaje) {
	    log.error("{} {} | {} | {} | {} | {} | {}", cuo, ErrorDefault.TRAZA.getNombre(), codigoError, codigoMensaje, UtilInfraestructure.obtenerClaseMetodoLineaException(e), e.getMessage(), UtilInfraestructure.obtenerCausaException(e));
	}
	
	public static String obtenerCausaException(Exception e) {
		int causaRaiz = 10;
		String causaString = "";
		Throwable causa = e.getCause();
		for(int i=0;i<causaRaiz;i++) {
			if(causa!=null) {
				causaString = causaString + " " + causa;
				causa = causa.getCause();
			}else 
				break;
			
		}
		return causaString.trim().equalsIgnoreCase("") ? ErrorDefault.CAUSA_NO_IDENTIFICADA.getNombre() : causaString;
	}
	
	public static String obtenerClaseMetodoLineaException(Exception e) {
		String claseMetodoLineaString = "";
		StackTraceElement[] stackTrace = e.getStackTrace();
	    // Imprime la información de la clase y la línea donde se lanzó la excepción
	    if (stackTrace!=null && stackTrace.length > 0) {
	        StackTraceElement firstElement = stackTrace[0];
	        claseMetodoLineaString = firstElement.getClassName() + "::" + firstElement.getMethodName() + "::" + firstElement.getLineNumber();
	    }
		return claseMetodoLineaString.trim().equalsIgnoreCase("") ? ErrorDefault.CLASE_METODO_LINEA_NO_IDENTIFICADO.getNombre() : claseMetodoLineaString;
	}
	
}
