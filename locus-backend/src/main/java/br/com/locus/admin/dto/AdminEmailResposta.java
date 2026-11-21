package br.com.locus.admin.dto;

public record AdminEmailResposta(
        String email,
        boolean contaEncontrada,
        Long usuarioId,
        String nome) {
}
