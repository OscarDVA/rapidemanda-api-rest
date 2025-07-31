package pe.gob.pj.rapidemanda.domain.exceptions;

import java.io.Serializable;

import lombok.Getter;

public class ErrorDaoException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter
	protected String codigo;
	@Getter
	protected String descripcion;
	@Getter
	protected String descripcionErrorDB;
	@Getter
	protected String descripcionErrorSP;
	
	public ErrorDaoException(String codigo, String descripcion, String message, Throwable cause) {
		super(message, cause);
		this.codigo=codigo;
		this.descripcion=descripcion;
	}
	
	public ErrorDaoException(String codigo, String descripcion, String descripcionErrorDB, String message, Throwable cause) {
		super(message, cause);
		this.codigo=codigo;
		this.descripcion=descripcion;
		this.descripcionErrorDB=descripcionErrorDB;
	}
	
	public ErrorDaoException(String codigo, String descripcion, String descripcionErrorDB, String descripcionErrorSP, String message, Throwable cause) {
		super(message, cause);
		this.codigo=codigo;
		this.descripcion=descripcion;
		this.descripcionErrorDB=descripcionErrorDB;
		this.descripcionErrorSP=descripcionErrorSP;
	}

}
