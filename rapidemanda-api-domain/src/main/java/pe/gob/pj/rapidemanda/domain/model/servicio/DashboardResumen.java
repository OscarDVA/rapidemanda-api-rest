package pe.gob.pj.rapidemanda.domain.model.servicio;

import java.io.Serializable;

import lombok.Data;

@Data
public class DashboardResumen implements Serializable {
    private static final long serialVersionUID = 1L;

    private long totalDemandas;
    private long totalDemandasDeltaMensual;
    private long totalRegistrados;
    private long totalRegistradosDeltaMensual;
    private long totalPresentacionM;
    private long totalPresentacionMDeltaMensual;
    private long totalPresentacionF;
    private long totalPresentacionFDeltaMensual;
}