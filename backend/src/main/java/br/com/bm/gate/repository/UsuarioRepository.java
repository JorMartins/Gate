package br.com.bm.gate.repository;

import br.com.bm.gate.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar usuário por nome
    Optional<Usuario> findByNome(String nome);

    // Buscar usuários por tipo
    List<Usuario> findByTipo(String tipo);

    // Buscar usuários por nome (case insensitive)
    List<Usuario> findByNomeContainingIgnoreCase(String nome);

    // Verificar se existe usuário com nome
    boolean existsByNome(String nome);
}