package br.com.bm.gate.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_gate_log")
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date", nullable = false)
    private LocalDateTime data;

    @Column(name = "operador", length = 255, nullable = false)
    private String operador;

    @Column(name = "log", length = 255, nullable = false)
    private String log;

    // Construtores
    public Log() {}

    public Log(LocalDateTime data, String operador, String log) {
        this.data = data;
        this.operador = operador;
        this.log = log;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}