package br.com.bm.gate.dto;

public class DispositivoDTO {
    private Long idDispositivo;
    private String descricao;
    private String imei;

    // Construtores, getters e setters
    public DispositivoDTO() {}

    public Long getIdDispositivo() { return idDispositivo; }
    public void setIdDispositivo(Long idDispositivo) { this.idDispositivo = idDispositivo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getImei() { return imei; }
    public void setImei(String imei) { this.imei = imei; }
}