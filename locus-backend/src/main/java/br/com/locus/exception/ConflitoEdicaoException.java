package br.com.locus.exception;

public class ConflitoEdicaoException extends RuntimeException {

    public ConflitoEdicaoException(String mensagem) {
        super(mensagem);
    }
}