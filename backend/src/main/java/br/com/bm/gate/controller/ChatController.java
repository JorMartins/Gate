package br.com.bm.gate.controller;

import br.com.bm.gate.dto.ChatDTO;
import br.com.bm.gate.model.Chat;
import br.com.bm.gate.service.ChatService;
import br.com.bm.gate.service.DtoConverterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chats")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private DtoConverterService dtoConverter;

    @GetMapping("/dispositivo/{idDispositivo}")
    public ResponseEntity<List<ChatDTO>> getChatsPorDispositivo(@PathVariable Long idDispositivo) {
        try {
            // ALTERAÇÃO: usar o novo metodo que busca apenas chats ativos
            List<Chat> chats = chatService.findChatsAtivosPorDispositivo(idDispositivo);
            List<ChatDTO> chatDTOs = chats.stream()
                    .map(dtoConverter::toChatDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(chatDTOs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/ativos-sem-dispositivo")
    public ResponseEntity<List<ChatDTO>> getChatsAtivosSemDispositivo() {
        try {
            List<Chat> chats = chatService.findChatsAtivosSemDispositivo();
            List<ChatDTO> chatDTOs = chats.stream()
                    .map(dtoConverter::toChatDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(chatDTOs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{idChat}/vincular-dispositivo/{idDispositivo}")
    public ResponseEntity<ChatDTO> vincularDispositivo(@PathVariable Long idChat, @PathVariable Long idDispositivo) {
        try {
            Chat chatAtualizado = chatService.vincularDispositivo(idChat, idDispositivo);
            ChatDTO chatDTO = dtoConverter.toChatDTO(chatAtualizado);
            return ResponseEntity.ok(chatDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{idChat}/finalizar")
    public ResponseEntity<ChatDTO> finalizarChat(@PathVariable Long idChat) {
        try {
            Chat chatFinalizado = chatService.finalizarChat(idChat);
            ChatDTO chatDTO = dtoConverter.toChatDTO(chatFinalizado);
            return ResponseEntity.ok(chatDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/ativos")
    public ResponseEntity<List<ChatDTO>> getChatsAtivos() {
        try {
            List<Chat> chats = chatService.findChatsAtivos();
            List<ChatDTO> chatDTOs = chats.stream()
                    .map(dtoConverter::toChatDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(chatDTOs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }




}