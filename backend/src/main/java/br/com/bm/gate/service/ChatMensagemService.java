package br.com.bm.gate.service;

import br.com.bm.gate.model.Chat;
import br.com.bm.gate.model.ChatMensagem;
import br.com.bm.gate.repository.ChatMensagemRepository;
import br.com.bm.gate.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatMensagemService {

    @Autowired
    private ChatMensagemRepository chatMensagemRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Transactional
    public ChatMensagem salvar(ChatMensagem chatMensagem) {
        // Verifica se o chat existe
        Chat chat = chatRepository.findById(chatMensagem.getChat().getIdChat())
                .orElseThrow(() -> new RuntimeException("Chat não encontrado"));
        chatMensagem.setChat(chat);

        return chatMensagemRepository.save(chatMensagem);
    }

    @Transactional
    public ChatMensagem salvarMensagem(Long idChat, String operador, String mensagem) {
        Chat chat = chatRepository.findById(idChat)
                .orElseThrow(() -> new RuntimeException("Chat não encontrado"));

        ChatMensagem novaMensagem = new ChatMensagem();
        novaMensagem.setChat(chat);
        novaMensagem.setOperador(operador);
        novaMensagem.setMensagem(mensagem);
        novaMensagem.setData(LocalDateTime.now());

        return chatMensagemRepository.save(novaMensagem);
    }

    @Transactional(readOnly = true)
    public List<ChatMensagem> listarTodos() {
        return chatMensagemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ChatMensagem> buscarPorChat(Long idChat) {
        Chat chat = chatRepository.findById(idChat)
                .orElseThrow(() -> new RuntimeException("Chat não encontrado"));
        return chatMensagemRepository.findByChatOrderByDataAsc(chat);
    }

    @Transactional(readOnly = true)
    public List<ChatMensagem> buscarPorOperador(String operador) {
        return chatMensagemRepository.findByOperador(operador);
    }

    @Transactional(readOnly = true)
    public List<ChatMensagem> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return chatMensagemRepository.findByDataBetween(inicio, fim);
    }

    @Transactional(readOnly = true)
    public List<ChatMensagem> buscarMensagensComTexto(String texto) {
        return chatMensagemRepository.findByMensagemContainingIgnoreCase(texto);
    }

    @Transactional(readOnly = true)
    public List<ChatMensagem> buscarUltimasMensagens(Long idChat, int quantidade) {
        Chat chat = chatRepository.findById(idChat)
                .orElseThrow(() -> new RuntimeException("Chat não encontrado"));
        return chatMensagemRepository.findUltimasMensagensByChat(chat, quantidade);
    }

    @Transactional(readOnly = true)
    public Long contarMensagensPorChat(Long idChat) {
        Chat chat = chatRepository.findById(idChat)
                .orElseThrow(() -> new RuntimeException("Chat não encontrado"));
        return chatMensagemRepository.countByChat(chat);
    }

    @Transactional(readOnly = true)
    public List<ChatMensagem> buscarPorOperadorEPeriodo(String operador, LocalDateTime inicio, LocalDateTime fim) {
        return chatMensagemRepository.findByOperadorAndPeriodo(operador, inicio, fim);
    }

    @Transactional
    public void excluir(Long id) {
        chatMensagemRepository.deleteById(id);
    }
}