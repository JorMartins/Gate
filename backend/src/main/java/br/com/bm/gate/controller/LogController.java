package br.com.bm.gate.controller;

import br.com.bm.gate.model.Log;
import br.com.bm.gate.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    @Autowired
    private LogService logService;

    @PostMapping
    public ResponseEntity<Log> criarLog(@RequestBody Log log) {
        try {
            Log logSalvo = logService.salvar(log);
            return new ResponseEntity<>(logSalvo, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<Log> registrarLog(
            @RequestParam String operador,
            @RequestParam String acao) {
        try {
            Log logRegistrado = logService.registrarLog(operador, acao);
            return new ResponseEntity<>(logRegistrado, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<Log>> listarLogs() {
        try {
            List<Log> logs = logService.listarTodos();
            return new ResponseEntity<>(logs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/operador/{operador}")
    public ResponseEntity<List<Log>> buscarLogsPorOperador(@PathVariable String operador) {
        try {
            List<Log> logs = logService.buscarPorOperador(operador);
            return new ResponseEntity<>(logs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<Log>> buscarLogsPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        try {
            List<Log> logs = logService.buscarPorPeriodo(inicio, fim);
            return new ResponseEntity<>(logs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/texto/{texto}")
    public ResponseEntity<List<Log>> buscarLogsComTexto(@PathVariable String texto) {
        try {
            List<Log> logs = logService.buscarLogsComTexto(texto);
            return new ResponseEntity<>(logs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/ultimos/{quantidade}")
    public ResponseEntity<List<Log>> buscarUltimosLogs(@PathVariable int quantidade) {
        try {
            List<Log> logs = logService.buscarUltimosLogs(quantidade);
            return new ResponseEntity<>(logs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/operador-parcial/{operadorParcial}")
    public ResponseEntity<List<Log>> buscarLogsPorOperadorParcial(@PathVariable String operadorParcial) {
        try {
            List<Log> logs = logService.buscarPorOperadorParcial(operadorParcial);
            return new ResponseEntity<>(logs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/estatisticas/operador")
    public ResponseEntity<List<Object[]>> obterEstatisticasPorOperador() {
        try {
            List<Object[]> estatisticas = logService.obterEstatisticasPorOperador();
            return new ResponseEntity<>(estatisticas, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> excluirLog(@PathVariable Long id) {
        try {
            logService.excluir(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}