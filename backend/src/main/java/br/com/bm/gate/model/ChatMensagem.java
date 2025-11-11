package br.com.bm.gate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_gate_chat_mensagem")
public class ChatMensagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_chat_mensagem")
    private Long idChatMensagem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chat", nullable = false)
    @JsonIgnore
    private Chat chat;

    @Column(name = "data", nullable = false)
    private LocalDateTime data;

    @Column(name = "operador", nullable = false, length = 255)
    private String operador;

    @Column(name = "mensagem", nullable = false, length = 255)
    private String mensagem;

    // Construtores
    public ChatMensagem() {}

    public ChatMensagem(Chat chat, LocalDateTime data, String operador, String mensagem) {
        this.chat = chat;
        this.data = data;
        this.operador = operador;
        this.mensagem = mensagem;
    }

    // Getters e Setters
    public Long getIdChatMensagem() {
        return idChatMensagem;
    }

    public void setIdChatMensagem(Long idChatMensagem) {
        this.idChatMensagem = idChatMensagem;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}