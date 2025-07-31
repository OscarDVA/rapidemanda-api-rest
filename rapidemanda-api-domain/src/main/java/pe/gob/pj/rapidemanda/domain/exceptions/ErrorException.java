package pe.gob.pj.rapidemanda.domain.exceptions;

import java.io.Serializable;

import lombok.Getter;

@Getter
public class ErrorException extends Exception implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String codigo;
	private String descripcion;
	
	public ErrorException(String codigo, String descripcion) {
		super(descripcion);
		this.codigo=codigo;
		this.descripcion=descripcion;
	}
	
	public ErrorException(String codigo, String descripcion, Throwable cause) {
		super(cause);
		this.codigo=codigo;
		this.descripcion=descripcion;
	}
	
	public ErrorException(String codigo, String descripcion, String message, Throwable cause) {
		super(message, cause);
		this.codigo=codigo;
		this.descripcion=descripcion;
	}
	

}
