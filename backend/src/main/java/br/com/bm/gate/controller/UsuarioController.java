package br.com.bm.gate.controller;

import br.com.bm.gate.model.Usuario;
import br.com.bm.gate.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        try {
            List<Usuario> usuarios = usuarioService.listarTodos();
            return new ResponseEntity<>(usuarios, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<Usuario> buscarUsuarioPorNome(@PathVariable String nome) {
        try {
            Optional<Usuario> usuario = usuarioService.buscarPorNome(nome);
            return usuario.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/existe/{nome}")
    public ResponseEntity<Boolean> verificarExistenciaPorNome(@PathVariable String nome) {
        try {
            boolean existe = usuarioService.existePorNome(nome);
            return new ResponseEntity<>(existe, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}