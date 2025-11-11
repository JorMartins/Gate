package br.com.bm.gate.controller;

import br.com.bm.gate.model.Dispositivo;
import br.com.bm.gate.service.DispositivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/dispositivos")
public class DispositivoController {

    @Autowired
    private DispositivoService dispositivoService;

    @PostMapping
    public ResponseEntity<Dispositivo> criarDispositivo(@RequestBody Dispositivo dispositivo) {
        try {
            Dispositivo dispositivoSalvo = dispositivoService.salvar(dispositivo);
            return new ResponseEntity<>(dispositivoSalvo, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<Dispositivo>> listarDispositivos() {
        try {
            List<Dispositivo> dispositivos = dispositivoService.listarTodos();
            return new ResponseEntity<>(dispositivos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dispositivo> buscarDispositivoPorId(@PathVariable Long id) {
        try {
            Optional<Dispositivo> dispositivo = dispositivoService.buscarPorId(id);
            return dispositivo.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/imei/{imei}")
    public ResponseEntity<Dispositivo> buscarDispositivoPorImei(@PathVariable String imei) {
        try {
            Optional<Dispositivo> dispositivo = dispositivoService.buscarPorImei(imei);
            return dispositivo.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/descricao/{descricao}")
    public ResponseEntity<List<Dispositivo>> buscarDispositivosPorDescricao(@PathVariable String descricao) {
        try {
            List<Dispositivo> dispositivos = dispositivoService.buscarPorDescricao(descricao);
            return new ResponseEntity<>(dispositivos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/chats-ativos")
    public ResponseEntity<List<Dispositivo>> buscarDispositivosComChatsAtivos() {
        try {
            List<Dispositivo> dispositivos = dispositivoService.buscarDispositivosComChatsAtivos();
            return new ResponseEntity<>(dispositivos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Dispositivo> atualizarDispositivo(@PathVariable Long id, @RequestBody Dispositivo dispositivo) {
        try {
            Dispositivo dispositivoAtualizado = dispositivoService.atualizar(id, dispositivo);
            return new ResponseEntity<>(dispositivoAtualizado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> excluirDispositivo(@PathVariable Long id) {
        try {
            dispositivoService.excluir(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("{imei}")
    public ResponseEntity<Boolean> verificarExistenciaPorImei(@PathVariable String imei) {
        try {
            boolean existe = dispositivoService.existePorImei(imei);
            return new ResponseEntity<>(existe, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}