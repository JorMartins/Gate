package br.com.bm.gate.service;

import br.com.bm.gate.model.Usuario;
import br.com.bm.gate.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorNome(String nome) {
        return usuarioRepository.findByNome(nome);
    }

    public List<Usuario> buscarPorTipo(String tipo) {
        return usuarioRepository.findByTipo(tipo);
    }

    public List<Usuario> buscarPorNomeContendo(String nome) {
        return usuarioRepository.findByNomeContainingIgnoreCase(nome);
    }

    public boolean existePorNome(String nome) {
        return usuarioRepository.existsByNome(nome);
    }

    public Usuario salvar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public void excluir(Long id) {
        usuarioRepository.deleteById(id);
    }
}