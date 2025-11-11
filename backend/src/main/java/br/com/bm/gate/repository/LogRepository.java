package br.com.bm.gate.repository;

import br.com.bm.gate.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {

    // Buscar logs por operador
    List<Log> findByOperador(String operador);

    // Buscar logs por período
    List<Log> findByDataBetween(LocalDateTime inicio, LocalDateTime fim);

    // Buscar logs contendo texto específico
    List<Log> findByLogContainingIgnoreCase(String texto);

    // Buscar logs por operador e período
    List<Log> findByOperadorAndDataBetween(String operador, LocalDateTime inicio, LocalDateTime fim);

    // Buscar últimos logs
    @Query("SELECT l FROM Log l ORDER BY l.data DESC LIMIT :quantidade")
    List<Log> findUltimosLogs(@Param("quantidade") int quantidade);

    // Buscar logs por operador contendo (like)
    List<Log> findByOperadorContainingIgnoreCase(String operadorParcial);

    // Estatísticas de logs por operador
    @Query("SELECT l.operador, COUNT(l) FROM Log l GROUP BY l.operador")
    List<Object[]> countLogsByOperador();

    // Buscar logs com texto específico no log
    @Query("SELECT l FROM Log l WHERE LOWER(l.log) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<Log> findByTextoNoLog(@Param("texto") String texto);
}