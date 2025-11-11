package br.com.bm.gate.service;

import br.com.bm.gate.model.Chat;
import br.com.bm.gate.model.Dispositivo;
import br.com.bm.gate.repository.ChatRepository;
import br.com.bm.gate.repository.DispositivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private DispositivoRepository dispositivoRepository;

    public List<Chat> findChatsAtivosPorDispositivo(Long idDispositivo) {
        return chatRepository.findByDispositivoIdDispositivoAndDataFinalIsNull(idDispositivo);
    }
    public List<Chat> findByDispositivoId(Long idDispositivo) {
        return chatRepository.findByDispositivoIdDispositivo(idDispositivo);
    }

    // Lista todos os chats ativos (data_final IS NULL) - com ou sem dispositivo
    public List<Chat> findChatsAtivos() {
        return chatRepository.findByDataFinalIsNull();
    }

    // Lista apenas chats ativos sem dispositivo (para vincular)
    public List<Chat> findChatsAtivosSemDispositivo() {
        return chatRepository.findByDataFinalIsNullAndDispositivoIsNull();
    }

    public Chat vincularDispositivo(Long idChat, Long idDispositivo) {
        Optional<Chat> chatOptional = chatRepository.findById(idChat);
        Optional<Dispositivo> dispositivoOptional = dispositivoRepository.findById(idDispositivo);

        if (chatOptional.isPresent() && dispositivoOptional.isPresent()) {
            Chat chat = chatOptional.get();
            Dispositivo dispositivo = dispositivoOptional.get();

            chat.setDispositivo(dispositivo);
            return chatRepository.save(chat);
        } else {
            if (!chatOptional.isPresent()) {
                throw new RuntimeException("Chat não encontrado com ID: " + idChat);
            } else {
                throw new RuntimeException("Dispositivo não encontrado com ID: " + idDispositivo);
            }
        }
    }

    public Chat finalizarChat(Long idChat) {
        Optional<Chat> chatOptional = chatRepository.findById(idChat);

        if (chatOptional.isPresent()) {
            Chat chat = chatOptional.get();
            chat.setDataFinal(LocalDateTime.now());
            return chatRepository.save(chat);
        } else {
            throw new RuntimeException("Chat não encontrado com ID: " + idChat);
        }
    }
}