package br.com.locus.notificacao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacaoRepository
        extends JpaRepository<br.com.locus.notificacao.Notificacao, Long> {

    List<br.com.locus.notificacao.Notificacao>
    findByUsuarioIdOrderByDataCriacaoDesc(Long usuarioId);

    List<br.com.locus.notificacao.Notificacao>
    findByUsuarioIdAndLidaFalseOrderByDataCriacaoDesc(Long usuarioId);

    long countByUsuarioIdAndLidaFalse(Long usuarioId);
}