package pe.gob.pj.rapidemanda.infraestructure.enums;

import lombok.Getter;

@Getter
public enum PatternDatos {

	SEXO("[mfMF]"), 
	CELULAR("\\+?([0-9])+"), 
	NUMERICO("\\d+"), 
	ALFANUMERICO("[a-zA-Z0-9]+"),
	ALFANUMERICO_COMAS("[a-zA-Z0-9]+(,[a-zA-Z0-9]+)*"), 
	LETRAS("^[\\p{L}\\p{M}]+$"), LETRAS_ESPACIO("^[\\p{L}\\p{M}]+( [\\p{L}\\p{M}]+)*$"), 
	FLAG_SN("[snSN]"), FLAG_01("[10]"),
	EXPEDIENTE_FORMATO("(\\d{5})[-](\\d{4})[-](\\d{1,4})[-](\\d{4})[-]([A-Za-z]{2})[-]([A-Za-z]{2})[-](\\d{2})"),
	EMAIL("([A-Za-z0-9]+[._-]?[A-Za-z0-9]+)+@([A-Za-z0-9]+[-]?[A-Za-z0-9]+\\.)*[A-Za-z0-9]{2,6}"),
	IP("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$"),
	MAC("([0-9A-F]{2}[:-]){5}([0-9A-F]{2})"),
	FECHA_DEFAILT("([0][1-9]|[1-2][0-9]|[3][0-1])/([0][1-9]|[1][0-2])/(\\d{4})"),
	FECHA_DD_MM_YYYY_HH_MM_SS_SSS("(\\d{2})/(\\d{2})/(\\d{4}) (\\d{2}):(\\d{2}):(\\d{2})\\.(\\d{3})"),
	FECHA_YYYY_MM_DD_HH_MM_SS_SSS("(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2}):(\\d{2})\\.(\\d{3})");

	String nombre;

	PatternDatos(String nombre) {
		this.nombre = nombre;
	}

}
