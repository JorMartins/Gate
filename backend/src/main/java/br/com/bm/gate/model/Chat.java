package br.com.bm.gate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "t_gate_chat")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_chat")
    private Long idChat;

    @Column(name = "placa", length = 10, nullable = false)
    private String placa;

    @Column(name = "data_inicial", nullable = false)
    private LocalDateTime dataInicial;

    @Column(name = "data_final", nullable = false)
    private LocalDateTime dataFinal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dispositivo", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Dispositivo dispositivo;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ChatMensagem> mensagens = new ArrayList<>();

    // Construtores
    public Chat() {}

    public Chat(String placa, LocalDateTime dataInicial, LocalDateTime dataFinal, Dispositivo dispositivo) {
        this.placa = placa;
        this.dataInicial = dataInicial;
        this.dataFinal = dataFinal;
        this.dispositivo = dispositivo;
    }

    // Getters e Setters
    public Long getIdChat() {
        return idChat;
    }

    public void setIdChat(Long idChat) {
        this.idChat = idChat;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public LocalDateTime getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(LocalDateTime dataInicial) {
        this.dataInicial = dataInicial;
    }

    public LocalDateTime getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(LocalDateTime dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Dispositivo getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(Dispositivo dispositivo) {
        this.dispositivo = dispositivo;
    }

    public List<ChatMensagem> getMensagens() {
        return mensagens;
    }

    public void setMensagens(List<ChatMensagem> mensagens) {
        this.mensagens = mensagens;
    }
}