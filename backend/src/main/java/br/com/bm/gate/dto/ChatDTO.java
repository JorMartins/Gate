package br.com.bm.gate.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatDTO {
    private Long idChat;
    private String placa;
    private LocalDateTime dataInicial;
    private LocalDateTime dataFinal;
    private DispositivoDTO dispositivo;
    private List<MensagemDTO> mensagens = new ArrayList<>();

    // Construtores, getters e setters
    public ChatDTO() {}

    public Long getIdChat() { return idChat; }
    public void setIdChat(Long idChat) { this.idChat = idChat; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public LocalDateTime getDataInicial() { return dataInicial; }
    public void setDataInicial(LocalDateTime dataInicial) { this.dataInicial = dataInicial; }

    public LocalDateTime getDataFinal() { return dataFinal; }
    public void setDataFinal(LocalDateTime dataFinal) { this.dataFinal = dataFinal; }

    public DispositivoDTO getDispositivo() { return dispositivo; }
    public void setDispositivo(DispositivoDTO dispositivo) { this.dispositivo = dispositivo; }

    public List<MensagemDTO> getMensagens() { return mensagens; }
    public void setMensagens(List<MensagemDTO> mensagens) { this.mensagens = mensagens; }
}