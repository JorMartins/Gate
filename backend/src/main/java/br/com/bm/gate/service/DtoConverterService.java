package br.com.bm.gate.service;

import br.com.bm.gate.dto.ChatDTO;
import br.com.bm.gate.dto.DispositivoDTO;
import br.com.bm.gate.dto.MensagemDTO;
import br.com.bm.gate.model.Chat;
import br.com.bm.gate.model.ChatMensagem;
import br.com.bm.gate.model.Dispositivo;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;


@Service
public class DtoConverterService {

    public ChatDTO toChatDTO(Chat chat) {
        if (chat == null) {
            return null;
        }

        ChatDTO dto = new ChatDTO();
        dto.setIdChat(chat.getIdChat());
        dto.setPlaca(chat.getPlaca());
        dto.setDataInicial(chat.getDataInicial());
        dto.setDataFinal(chat.getDataFinal());

        if (chat.getDispositivo() != null) {
            dto.setDispositivo(toDispositivoDTO(chat.getDispositivo()));
        }

        // Converter apenas as mensagens, SEM incluir o chat novamente
        if (chat.getMensagens() != null && !chat.getMensagens().isEmpty()) {
            dto.setMensagens(chat.getMensagens().stream()
                    .map(this::toMensagemDTOSemChat) // ← Use um método diferente
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public MensagemDTO toMensagemDTO(ChatMensagem mensagem) {
        if (mensagem == null) {
            return null;
        }

        MensagemDTO dto = new MensagemDTO();
        dto.setIdChatMensagem(mensagem.getIdChatMensagem());
        dto.setMensagem(mensagem.getMensagem());
        dto.setData(mensagem.getData());
        dto.setOperador(mensagem.getOperador());

        // Apenas o ID do chat para evitar recursão
        if (mensagem.getChat() != null) {
            dto.setIdChat(mensagem.getChat().getIdChat());
        }

        return dto;
    }

    // Método adicional para evitar recursão
    private MensagemDTO toMensagemDTOSemChat(ChatMensagem mensagem) {
        if (mensagem == null) {
            return null;
        }

        MensagemDTO dto = new MensagemDTO();
        dto.setIdChatMensagem(mensagem.getIdChatMensagem());
        dto.setMensagem(mensagem.getMensagem());
        dto.setData(mensagem.getData());
        dto.setOperador(mensagem.getOperador());
        dto.setIdChat(mensagem.getChat() != null ? mensagem.getChat().getIdChat() : null);

        return dto;
    }

    public DispositivoDTO toDispositivoDTO(Dispositivo dispositivo) {
        if (dispositivo == null) {
            return null;
        }

        DispositivoDTO dto = new DispositivoDTO();
        dto.setIdDispositivo(dispositivo.getIdDispositivo());
        dto.setDescricao(dispositivo.getDescricao());
        dto.setImei(dispositivo.getImei());
        return dto;
    }
}