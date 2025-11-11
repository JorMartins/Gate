package br.com.bm.gate.repository;

import br.com.bm.gate.model.Chat;
import br.com.bm.gate.model.ChatMensagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMensagemRepository extends JpaRepository<ChatMensagem, Long> {

    // Buscar mensagens por chat
    List<ChatMensagem> findByChat(Chat chat);

    // Buscar mensagens por operador
    List<ChatMensagem> findByOperador(String operador);

    // Buscar mensagens por período
    List<ChatMensagem> findByDataBetween(LocalDateTime inicio, LocalDateTime fim);

    // Buscar mensagens por chat ordenadas por data
    List<ChatMensagem> findByChatOrderByDataAsc(Chat chat);

    // Buscar mensagens contendo texto específico
    List<ChatMensagem> findByMensagemContainingIgnoreCase(String texto);

    // Buscar últimas mensagens de um chat
    @Query("SELECT cm FROM ChatMensagem cm WHERE cm.chat = :chat ORDER BY cm.data DESC LIMIT :quantidade")
    List<ChatMensagem> findUltimasMensagensByChat(
            @Param("chat") Chat chat,
            @Param("quantidade") int quantidade
    );

    // Contar mensagens por chat
    Long countByChat(Chat chat);

    // Buscar mensagens por operador e período
    @Query("SELECT cm FROM ChatMensagem cm WHERE cm.operador = :operador AND cm.data BETWEEN :inicio AND :fim")
    List<ChatMensagem> findByOperadorAndPeriodo(
            @Param("operador") String operador,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );
}