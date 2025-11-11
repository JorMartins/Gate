package br.com.bm.gate.repository;

import br.com.bm.gate.model.Dispositivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DispositivoRepository extends JpaRepository<Dispositivo, Long> {

    // Buscar dispositivo por IMEI
    Optional<Dispositivo> findByImei(String imei);

    // Buscar dispositivos por descrição (case insensitive)
    List<Dispositivo> findByDescricaoContainingIgnoreCase(String descricao);

    // Verificar se existe dispositivo com IMEI
    boolean existsByImei(String imei);

    // Buscar dispositivos com chats ativos
    @Query("SELECT d FROM Dispositivo d WHERE SIZE(d.chats) > 0")
    List<Dispositivo> findDispositivosComChatsAtivos();

    // Buscar por IMEI parcial
    @Query("SELECT d FROM Dispositivo d WHERE d.imei LIKE %:imeiParcial%")
    List<Dispositivo> findByImeiContaining(@Param("imeiParcial") String imeiParcial);
}