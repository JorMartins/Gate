package br.com.bm.gate.service;

import br.com.bm.gate.model.Dispositivo;
import br.com.bm.gate.repository.DispositivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DispositivoService {

    @Autowired
    private DispositivoRepository dispositivoRepository;

    @Transactional
    public Dispositivo salvar(Dispositivo dispositivo) {
        return dispositivoRepository.save(dispositivo);
    }

    @Transactional(readOnly = true)
    public List<Dispositivo> listarTodos() {
        return dispositivoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Dispositivo> buscarPorId(Long id) {
        return dispositivoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Dispositivo> buscarPorImei(String imei) {
        return dispositivoRepository.findByImei(imei);
    }

    @Transactional(readOnly = true)
    public List<Dispositivo> buscarPorDescricao(String descricao) {
        return dispositivoRepository.findByDescricaoContainingIgnoreCase(descricao);
    }

    @Transactional(readOnly = true)
    public boolean existePorImei(String imei) {
        return dispositivoRepository.existsByImei(imei);
    }

    @Transactional(readOnly = true)
    public List<Dispositivo> buscarDispositivosComChatsAtivos() {
        return dispositivoRepository.findDispositivosComChatsAtivos();
    }

    @Transactional
    public void excluir(Long id) {
        dispositivoRepository.deleteById(id);
    }

    @Transactional
    public Dispositivo atualizar(Long id, Dispositivo dispositivoAtualizado) {
        return dispositivoRepository.findById(id)
                .map(dispositivo -> {
                    dispositivo.setDescricao(dispositivoAtualizado.getDescricao());
                    dispositivo.setImei(dispositivoAtualizado.getImei());
                    return dispositivoRepository.save(dispositivo);
                })
                .orElseThrow(() -> new RuntimeException("Dispositivo não encontrado com ID: " + id));
    }
}