package br.com.bm.gate.repository;

import br.com.bm.gate.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findByDispositivoIdDispositivo(Long idDispositivo);

    // Busca todos os chats ativos (data_final IS NULL)
    List<Chat> findByDataFinalIsNull();

    // Busca chats ativos sem dispositivo vinculado
    List<Chat> findByDataFinalIsNullAndDispositivoIsNull();

    // NOVO: Busca chats ativos por dispositivo específico
    List<Chat> findByDispositivoIdDispositivoAndDataFinalIsNull(Long idDispositivo);
}