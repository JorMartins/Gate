package br.com.bm.gate.dto;

import java.time.LocalDateTime;

public class MensagemDTO {
    private Long idChatMensagem;
    private String mensagem;
    private LocalDateTime data;
    private String operador;
    private Long idChat;

    // Construtores, getters e setters
    public MensagemDTO() {}

    public Long getIdChatMensagem() { return idChatMensagem; }
    public void setIdChatMensagem(Long idChatMensagem) { this.idChatMensagem = idChatMensagem; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }

    public String getOperador() { return operador; }
    public void setOperador(String operador) { this.operador = operador; }

    public Long getIdChat() { return idChat; }
    public void setIdChat(Long idChat) { this.idChat = idChat; }
}