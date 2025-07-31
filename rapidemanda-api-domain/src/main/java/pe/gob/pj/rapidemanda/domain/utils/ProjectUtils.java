package pe.gob.pj.rapidemanda.domain.utils;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.text.Normalizer;
import java.util.regex.Pattern;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate5.SessionFactoryUtils;


public class ProjectUtils {
	
	public static String sessionFactoryToJNDI(SessionFactory sf) throws SQLException {
		return (SessionFactoryUtils.getDataSource(sf)).getConnection().getMetaData().getURL();
	}

	public static boolean isNullOrEmpty(Object valor) {
		boolean flag = false;
		if (valor == null) {
			flag = true;
		} else if ((String.valueOf(valor)).trim().equalsIgnoreCase("") || (String.valueOf(valor)).trim().equalsIgnoreCase("null")) {
			flag = true;
		}
		return flag;
	}
	
	public static boolean tieneTamanioValido(String cadena, int tamanio) {
		boolean valido = false;
		if (cadena != null) {
			valido = cadena.trim().length()>0 && cadena.trim().length() <= tamanio;
		}
		return valido;
	}

	public static boolean esEntero(String entrada) {
		try {
			Integer.parseInt(entrada);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean esLong(String entrada) {
		try {
			Long.parseLong(entrada);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean esBigDecimal(String entrada) {
		try {
			new BigDecimal(entrada);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean esFechaValida(String dateToValidate, String dateFormat) {
		if (dateToValidate == null) {
			return false;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		sdf.setLenient(false);
		try {
			@SuppressWarnings("unused")
			Date date = sdf.parse(dateToValidate);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}
	
	private static double rad(double d) {  
	   return d * Math.PI / 180.0;  
	} 
	
	public static double distanciaCoordenadas(double latitud1, double longitud1, double latitud2, double longitud2) {
		double EARTH_RADIUS = 6378137;
		double radLat1 = rad(latitud1);  
	    double radLat2 = rad(latitud2);  
	    double a = radLat1 - radLat2;  
	    double b = rad(longitud1) - rad(longitud2);  
	    double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2)+Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));  
	    s = s * EARTH_RADIUS;
		return s;
	}

	public static String obtenerCodigoUnico() {
		Date fechaActual = new Date();
		SimpleDateFormat formato = new SimpleDateFormat("yyyyMMddHHmmssSSSSSS");
		String strFechaActual = formato.format(fechaActual);
		Random random = new Random();
		int aleatorio = random.nextInt(999) + 1;
		StringBuilder cuo = new StringBuilder();
		cuo.append(strFechaActual).append(String.valueOf(aleatorio));
		return cuo.toString();
	}

	public static Timestamp getFechaActualTimestamp() {
		Date date = new Date();
		java.sql.Timestamp timeStampDate = new Timestamp(date.getTime());
		return timeStampDate;
	}

	public static java.sql.Date getFechaActualDate() {
		Calendar c = Calendar.getInstance();
		java.sql.Date date = new java.sql.Date(c.getTime().getTime());
		return date;
	}
	
	public static String getMesLetra(int mesNumero) {
		String mesLetra = "No existe!";
		switch (mesNumero) {
		case 1:
			mesLetra = "Enero";
			break;
		case 2:
			mesLetra = "Febrero";
			break;
		case 3:
			mesLetra = "Marzo";
			break;
		case 4:
			mesLetra = "Abril";
			break;
		case 5:
			mesLetra = "Mayo";
			break;
		case 6:
			mesLetra = "Junio";
			break;
		case 7:
			mesLetra = "Julio";
			break;
		case 8:
			mesLetra = "Agosto";
			break;
		case 9:
			mesLetra = "Septiembre";
			break;
		case 10:
			mesLetra = "Octubre";
			break;
		case 11:
			mesLetra = "Noviembre";
			break;
		case 12:
			mesLetra = "Diciembre";
			break;
		}
		return mesLetra;
	}

	public static String convertDateToString(Date fecha, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		if(fecha!=null)
			return sdf.format(fecha);
		else
			return "";
	}
	
	public static Date sumarRestarSegundos(Date fecha, int segundos) {
		Calendar c = Calendar.getInstance();
		c.setTime(fecha);
		c.add(Calendar.SECOND, segundos);
		return c.getTime();
	}

	public static Date sumarRestarDias(Date fecha, int dias) {
		Calendar c = Calendar.getInstance();
		c.setTime(fecha);
		c.add(Calendar.DAY_OF_YEAR, dias);
		return c.getTime();
	}

	public static Date sumarRestarMeses(Date fecha, int meses) {
		Calendar c = Calendar.getInstance();
		c.setTime(fecha);
		c.add(Calendar.MONTH, meses);
		return c.getTime();
	}

	public static Date sumarRestarAnios(Date fecha, int anios) {
		Calendar c = Calendar.getInstance();
		c.setTime(fecha);
		c.add(Calendar.YEAR, anios);
		return c.getTime();
	}

	public static void setParameter(CallableStatement cs, int index, int type, Object value) throws Exception {
		setParameter(cs, index, type, value, false);
	}

	public static void setParameter(CallableStatement cs, int index, int type, Object value,
			boolean parseDateToTimestamp) throws Exception {
		try {
			if (parseDateToTimestamp == true && type == Types.DATE) {
				type = Types.TIMESTAMP;
			}

			if (value == null || value.toString().length() == 0) {
				cs.setNull(index, type);
				return;
			}
			switch (type) {
			case Types.VARCHAR:
				cs.setString(index, value.toString());
				break;
			case Types.CHAR:
				cs.setString(index, value.toString());
				break;
			case Types.INTEGER:
				cs.setInt(index, parseInteger(value));
				break;
			case Types.NUMERIC:
				cs.setBigDecimal(index, parseBigDecimal(value));
				break;
			case Types.DOUBLE:
				cs.setDouble(index, parseDouble(value));
				break;
			case Types.LONGVARCHAR:
				cs.setLong(index, parseLong(value));
				break;
			case Types.TIMESTAMP:
				if (value.getClass().getName().equals("java.util.Date")) {
					cs.setTimestamp(index, getSqlTimestamp((java.util.Date) value));
				} else if (value.getClass().getName().equals("java.sql.Timestamp")) {
					cs.setTimestamp(index, (java.sql.Timestamp) value);
				} else {
					cs.setTimestamp(index, getSqlTimestamp(value.toString()));
				}
				break;
			case Types.DATE:
				if (value.getClass().getName().equals("java.util.Date")) {
					cs.setDate(index, getSqlDate((java.util.Date) value));
					// stmt.setTimestamp(index,
					// getSqlTimestamp((java.util.Date)value));
				} else {
					cs.setDate(index, getSqlDate(value.toString()));
				}
				break;
			}
		} catch (Exception e) {
			cs.setNull(index, type);
		}
	}

	public static Date parseStringToDate(String fechaString, String format) throws Exception {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, new Locale("es", "ES"));
		Date fechaDate = null;
		try {
			fechaDate = simpleDateFormat.parse(fechaString);
		} catch (ParseException e) {
			throw new Exception(ProjectConstants.Mensajes.MSG_ERROR_GENERICO_CONVERSION + " [" + e.getMessage() + "]");
		}
		return fechaDate;
	}

	public static void getParameter(CallableStatement cs, int index, int type) throws Exception {

		try {
			switch (type) {
			case Types.VARCHAR:
				cs.registerOutParameter(index, Types.VARCHAR);
				break;
			case Types.INTEGER:
				cs.registerOutParameter(index, Types.INTEGER);
				break;
			case Types.NUMERIC:
				cs.registerOutParameter(index, Types.NUMERIC);
				break;
			case Types.DOUBLE:
				cs.registerOutParameter(index, Types.DOUBLE);
				break;
			case Types.BIGINT:
				cs.registerOutParameter(index, Types.BIGINT);
				break;
			case Types.TIMESTAMP:
				cs.registerOutParameter(index, Types.TIMESTAMP);
				break;
			case Types.DATE:
				cs.registerOutParameter(index, Types.DATE);
				break;
			}
		} catch (Exception e) {
		}
	}

	/**
	 * Metodo que retorna una lista de objectos Maps que es recogido desde la base
	 * de datos. .
	 * 
	 * @param rs variable ResultSet de SQL
	 * @throws Exception
	 */
	public static List<Map<String, Object>> getMapedObjects(ResultSet rs) throws SQLException {
		List<Map<String, Object>> list = null;
		ResultSetMetaData rsmd = null;
		try {
			if (rs != null) {
				rsmd = rs.getMetaData();
				list = new ArrayList<>();
				while (rs.next()) {
					Map<String, Object> row = new HashMap<>();
					for (int j = 1; j <= rsmd.getColumnCount(); ++j) {
						// System.out.println(rsmd.getColumnLabel(j).toString());
						if (rs.getObject(j) != null) {
							row.put(rsmd.getColumnLabel(j), rs.getObject(j));
						} else {
							row.put(rsmd.getColumnLabel(j), "");
						}
					}
					list.add(row);
				}
			}
		} catch (SQLException e) {
			throw new SQLException("Error de SQL al mapear: " + e.getMessage());
		}
		return list;
	}

	public static Integer parseInteger(Object value) throws Exception {
		try {
			return Integer.parseInt(value.toString());
		} catch (Exception e) {
			throw new Exception(ProjectConstants.Mensajes.MSG_ERROR_GENERICO_CONVERSION + " [" + e.getMessage() + "]");
		}
	}

	public static BigDecimal parseBigDecimal(Object value) throws Exception {
		try {
			// return BigDecimal.valueOf(Double.parseDouble(value.toString()));
			return new BigDecimal(value.toString());
		} catch (Exception e) {
			throw new Exception(ProjectConstants.Mensajes.MSG_ERROR_GENERICO_CONVERSION + " [" + e.getMessage() + "]");
		}
	}

	public static Double parseDouble(Object value) throws Exception {
		try {
			return Double.parseDouble(value.toString());
		} catch (Exception e) {
			throw new Exception(ProjectConstants.Mensajes.MSG_ERROR_GENERICO_CONVERSION + " [" + e.getMessage() + "]");
		}
	}

	public static Long parseLong(Object value) throws Exception {
		try {
			return Long.parseLong(value.toString());
		} catch (Exception e) {
			throw new Exception(ProjectConstants.Mensajes.MSG_ERROR_GENERICO_CONVERSION + " [" + e.getMessage() + "]");
		}
	}

	public static java.sql.Date getSqlDate(String fecha) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		try {
			java.util.Date aDate = format.parse(fecha);
			java.text.SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			java.sql.Date sqlDate = java.sql.Date.valueOf(sdf.format(aDate));
			return sqlDate;
		} catch (ParseException e) {
			throw new Exception(ProjectConstants.Mensajes.MSG_ERROR_GENERICO_CONVERSION + " [" + e.getMessage() + "]");
		}
	}

	public static java.sql.Date getSqlDateFull(String fecha) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		try {
			java.util.Date aDate = format.parse(fecha);
			java.text.SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.sql.Date sqlDate = java.sql.Date.valueOf(sdf.format(aDate));
			return sqlDate;
		} catch (ParseException e) {
			throw new Exception(ProjectConstants.Mensajes.MSG_ERROR_GENERICO_CONVERSION + " [" + e.getMessage() + "]");
		}
	}

	public static Date getSqlDateFullDos(String fecha) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		try {
			java.util.Date aDate = format.parse(fecha);
			return aDate;
		} catch (ParseException e) {
			throw new Exception(ProjectConstants.Mensajes.MSG_ERROR_GENERICO_CONVERSION + " [" + e.getMessage() + "]");
		}
	}

	public static java.sql.Date getSqlDate(java.util.Date fecha) {
		try {
			return new java.sql.Date(fecha.getTime());
		} catch (Exception e) {
			return null;
		}
	}

	public static java.sql.Timestamp getSqlTimestamp(java.util.Date fecha) {
		try {
			return new java.sql.Timestamp(fecha.getTime());
		} catch (Exception e) {
			return null;
		}
	}

	public static java.sql.Timestamp getSqlTimestamp(String fecha) {
		try {
			return getSqlTimestamp(getSqlDateFull(fecha));
		} catch (Exception e) {
			return null;
		}
	}

	public static String getSqlDate(java.sql.Date fecha) {
		if (fecha == null) {
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date jDate = new java.util.Date(fecha.getTime());
		return format.format(jDate);
	}

	/**
	 * Si el objeto de entrada es nulo devuelve String "", caso contrario, devuelve
	 * el o.toString().trim()
	 * 
	 * @param o es el valor object
	 */
	public static String validateStr(Object o) {
		if (o == null) {
			return "";
		} else {
			return o.toString().trim();
		}
	}

	/**
	 * Si el objeto de entrada es nulo devuelve un Objecto string "", caso
	 * contrario, devuelve el objecto mismo
	 * 
	 * @param o es el valor object
	 */
	public static Object validateObj(Object o) {
		if (o == null) {
			return "";
		} else {
			if (o instanceof String) {
				return ((String) o).trim();
			} else {
				return o;
			}
		}
	}

	/**
	 * Metodo que completa los a uns string la cantidad de caracteres indicada a la
	 * izquierda.
	 * 
	 * @param str es el objecto a completar
	 * @param len es la longitud solicitada
	 * @param pad caracter completado a la izquierda
	 */
	public static String lpad(Object str, int len, String pad) {
		String padTemp = "";
		for (int i = 1; i < len; i++) {
			padTemp = padTemp + pad;
		}
		String unido = padTemp + str.toString();
		return (unido).substring(unido.length() - len);
	}

	public static String convertExceptionToString(Exception e) {
		String exception = "";
		if (e != null) {
			CharArrayWriter cw = new CharArrayWriter();
			PrintWriter w = new PrintWriter(cw);
			e.printStackTrace(w);
			w.close();
			exception = cw.toString();
		} else {
			exception = "SE HA PRODUCIDO UNA EXCEPTION NO IDENTIFICADA";
		}
		return exception;
	}

	public static String generateCodDataBase(String codDistrito, String codInstacia, String codEspecialidad) {
		String codBaseDatos = null;
		if (validateStr(codDistrito).length() > 0 && validateStr(codInstacia).length() > 0
				&& validateStr(codEspecialidad).length() > 0) {
			codBaseDatos = codDistrito + codInstacia + codEspecialidad;
		}
		return codBaseDatos;
	}

	public static String isNull(Object val) {
		if (val == null) {
			return "";
		} else {
			return val.toString();
		}
	}

	public static Integer isInt(Object val) {
		if (val == null) {
			return 0;
		} else {
			return Integer.parseInt(val.toString());
		}
	}

	public static long isLong(Object val) {
		if (val == null) {
			return 0L;
		} else {
			return Long.parseLong(val.toString());
		}
	}

	public static BigDecimal isBigDecimal(Object val) {
		if (val == null) {
			return BigDecimal.ZERO;
		} else {
			return new BigDecimal(val.toString());
		}
	}

	public static Timestamp isDate(Object val) throws Exception {
		if (val == null) {
			return null;
		} else {
			return new Timestamp(getSqlDateFullDos(val.toString()).getTime());
		}
	}

	public static String quitarCaracteres(String val) {
		if (val != null) {
			// val.replaceAll("[^a-zA-Z]+",""); //.replace("é", "e").replace("í",
			// "i").replace("ó", "o").replace("ú", "u").replace("ñ", "n");
			Normalizer.normalize(val, Normalizer.Form.NFD);
			val.replaceAll("[^\\p{ASCII}]", "");
			Pattern DIACRITICS_AND_FRIENDS = Pattern.compile("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");
			val = Normalizer.normalize(val, Normalizer.Form.NFD);
			val = DIACRITICS_AND_FRIENDS.matcher(val).replaceAll("");
		}
		return val;
	}

	public static String getPc() {
		String pc = null;
		try {
			InetAddress addr;
			addr = InetAddress.getLocalHost();
			pc = addr.getHostName();
		} catch (UnknownHostException e) {
			pc = "host";
		}

		return pc;
	}

	public static String getIp() {
		String ip = null;
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			ip = "0.0.0.0";
		}

		return ip;
	}
	
	public static String getNombreRed() {
		String nombreRed = "Desconocido";
		try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                    	nombreRed = networkInterface.getDisplayName();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return nombreRed.length()<30?nombreRed:nombreRed.substring(0, 29);
	}

	public static String getMac() {
		String firstInterface = null;
		Map<String, String> addressByNetwork = new HashMap<>();
		Enumeration<NetworkInterface> networkInterfaces = null;
		try {
			networkInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (networkInterfaces != null) {
			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface network = networkInterfaces.nextElement();

				byte[] bmac = null;
				try {
					bmac = network.getHardwareAddress();
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (bmac != null) {
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < bmac.length; i++) {
						sb.append(String.format("%02X%s", bmac[i], (i < bmac.length - 1) ? "-" : ""));
					}

					if (sb.toString().isEmpty() == false) {
						addressByNetwork.put(network.getName(), sb.toString());

					}

					if (sb.toString().isEmpty() == false && firstInterface == null) {
						firstInterface = network.getName();
					}
				}
			}

			if (firstInterface != null) {
				return addressByNetwork.get(firstInterface);
			}
		}

		return firstInterface;
	}

}
