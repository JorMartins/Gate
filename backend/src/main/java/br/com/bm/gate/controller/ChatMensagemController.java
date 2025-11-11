package br.com.bm.gate.controller;

import br.com.bm.gate.model.ChatMensagem;
import br.com.bm.gate.service.ChatMensagemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/chat-mensagens")
public class ChatMensagemController {

    @Autowired
    private ChatMensagemService chatMensagemService;

    @PostMapping
    public ResponseEntity<ChatMensagem> criarMensagem(@RequestBody ChatMensagem chatMensagem) {
        try {
            ChatMensagem mensagemSalva = chatMensagemService.salvar(chatMensagem);
            return new ResponseEntity<>(mensagemSalva, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/chat/{idChat}")
    public ResponseEntity<ChatMensagem> criarMensagemParaChat(
            @PathVariable Long idChat,
            @RequestParam String operador,
            @RequestParam String mensagem) {
        try {
            ChatMensagem novaMensagem = chatMensagemService.salvarMensagem(idChat, operador, mensagem);
            return new ResponseEntity<>(novaMensagem, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<ChatMensagem>> listarMensagens() {
        try {
            List<ChatMensagem> mensagens = chatMensagemService.listarTodos();
            return new ResponseEntity<>(mensagens, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/chat/{idChat}")
    public ResponseEntity<List<ChatMensagem>> buscarMensagensPorChat(@PathVariable Long idChat) {
        try {
            List<ChatMensagem> mensagens = chatMensagemService.buscarPorChat(idChat);
            return new ResponseEntity<>(mensagens, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/operador/{operador}")
    public ResponseEntity<List<ChatMensagem>> buscarMensagensPorOperador(@PathVariable String operador) {
        try {
            List<ChatMensagem> mensagens = chatMensagemService.buscarPorOperador(operador);
            return new ResponseEntity<>(mensagens, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<ChatMensagem>> buscarMensagensPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        try {
            List<ChatMensagem> mensagens = chatMensagemService.buscarPorPeriodo(inicio, fim);
            return new ResponseEntity<>(mensagens, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/texto/{texto}")
    public ResponseEntity<List<ChatMensagem>> buscarMensagensComTexto(@PathVariable String texto) {
        try {
            List<ChatMensagem> mensagens = chatMensagemService.buscarMensagensComTexto(texto);
            return new ResponseEntity<>(mensagens, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/chat/{idChat}/ultimas/{quantidade}")
    public ResponseEntity<List<ChatMensagem>> buscarUltimasMensagens(
            @PathVariable Long idChat,
            @PathVariable int quantidade) {
        try {
            List<ChatMensagem> mensagens = chatMensagemService.buscarUltimasMensagens(idChat, quantidade);
            return new ResponseEntity<>(mensagens, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/chat/{idChat}/contagem")
    public ResponseEntity<Long> contarMensagensPorChat(@PathVariable Long idChat) {
        try {
            Long contagem = chatMensagemService.contarMensagensPorChat(idChat);
            return new ResponseEntity<>(contagem, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> excluirMensagem(@PathVariable Long id) {
        try {
            chatMensagemService.excluir(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}