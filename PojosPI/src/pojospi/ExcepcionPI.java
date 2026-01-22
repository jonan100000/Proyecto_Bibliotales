/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pojospi;

/**
 *
 * @author DosherGG
 */
public class ExcepcionPI extends Exception {
    private String sentenciaSQL;
    private Integer codigoErrorBD;
    private String mensajeErrorBD;
    private String mensajeErrorUsuario;

    public ExcepcionPI() {
    }

    public ExcepcionPI(String sentenciaSQL, Integer codigoErrorBD, String mensajeErrorBD, String mensajeErrorUsuario) {
        this.sentenciaSQL = sentenciaSQL;
        this.codigoErrorBD = codigoErrorBD;
        this.mensajeErrorBD = mensajeErrorBD;
        this.mensajeErrorUsuario = mensajeErrorUsuario;
    }

    public String getSentenciaSQL() {
        return sentenciaSQL;
    }

    public void setSentenciaSQL(String sentenciaSQL) {
        this.sentenciaSQL = sentenciaSQL;
    }

    public Integer getCodigoErrorBD() {
        return codigoErrorBD;
    }

    public void setCodigoErrorBD(Integer codigoErrorBD) {
        this.codigoErrorBD = codigoErrorBD;
    }

    public String getMensajeErrorBD() {
        return mensajeErrorBD;
    }

    public void setMensajeErrorBD(String mensajeErrorBD) {
        this.mensajeErrorBD = mensajeErrorBD;
    }

    public String getMensajeErrorUsuario() {
        return mensajeErrorUsuario;
    }

    public void setMensajeErrorUsuario(String mensajeErrorUsuario) {
        this.mensajeErrorUsuario = mensajeErrorUsuario;
    }

    @Override
    public String toString() {
        return "ExcepcionHR{" + "sentenciaSQL=" + sentenciaSQL + ", codigoErrorBD=" + codigoErrorBD + ", mensajeErrorBD=" + mensajeErrorBD + ", mensajeErrorUsuario=" + mensajeErrorUsuario + '}';
    }
}
