package br.com.bm.gate.service;

import br.com.bm.gate.model.Log;
import br.com.bm.gate.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogService {

    @Autowired
    private LogRepository logRepository;

    @Transactional
    public Log salvar(Log log) {
        return logRepository.save(log);
    }

    @Transactional
    public Log registrarLog(String operador, String acao) {
        Log log = new Log();
        log.setOperador(operador);
        log.setLog(acao);
        log.setData(LocalDateTime.now());
        return logRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<Log> listarTodos() {
        return logRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Log> buscarPorOperador(String operador) {
        return logRepository.findByOperador(operador);
    }

    @Transactional(readOnly = true)
    public List<Log> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return logRepository.findByDataBetween(inicio, fim);
    }

    @Transactional(readOnly = true)
    public List<Log> buscarLogsComTexto(String texto) {
        return logRepository.findByLogContainingIgnoreCase(texto);
    }

    @Transactional(readOnly = true)
    public List<Log> buscarUltimosLogs(int quantidade) {
        return logRepository.findUltimosLogs(quantidade);
    }

    @Transactional(readOnly = true)
    public List<Log> buscarPorOperadorEPeriodo(String operador, LocalDateTime inicio, LocalDateTime fim) {
        return logRepository.findByOperadorAndDataBetween(operador, inicio, fim);
    }

    @Transactional(readOnly = true)
    public List<Log> buscarPorOperadorParcial(String operadorParcial) {
        return logRepository.findByOperadorContainingIgnoreCase(operadorParcial);
    }

    @Transactional(readOnly = true)
    public List<Log> buscarPorTextoNoLog(String texto) {
        return logRepository.findByTextoNoLog(texto);
    }

    @Transactional(readOnly = true)
    public List<Object[]> obterEstatisticasPorOperador() {
        return logRepository.countLogsByOperador();
    }

    @Transactional
    public void excluir(Long id) {
        logRepository.deleteById(id);
    }

}