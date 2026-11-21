package br.com.locus.exception;

import br.com.locus.integracao.ExternalApiException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.validation.ConstraintViolationException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class TratadorGlobalException {

    @ExceptionHandler(ConflitoEdicaoException.class)
    public ResponseEntity<Map<String, Object>> tratarConflitoEdicao(
            ConflitoEdicaoException exception) {

        Map<String, Object> resposta = new HashMap<>();

        resposta.put("status", 409);
        resposta.put("mensagem", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(resposta);
    }

    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<Map<String, Object>> tratarAcessoNegado(
            AcessoNegadoException exception) {

        Map<String, Object> resposta = new HashMap<>();

        resposta.put("status", 403);
        resposta.put("mensagem", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(resposta);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> tratarRuntimeException(
            RuntimeException exception) {

        Map<String, Object> resposta = new HashMap<>();

        if (mensagemTecnica(exception.getMessage())) {
            resposta.put("status", 500);
            resposta.put("mensagem", "Não foi possível carregar seus dados agora. Tente novamente em instantes.");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(resposta);
        }

        resposta.put("status", 400);
        resposta.put("mensagem", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(resposta);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> tratarFalhaDePersistencia(
            DataAccessException exception) {

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("status", 500);
        resposta.put("mensagem", "Não foi possível carregar seus dados agora. Tente novamente em instantes.");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(resposta);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> tratarArgumentoInvalido(
            MethodArgumentNotValidException exception) {

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("status", 400);
        resposta.put("mensagem", "Os dados enviados são inválidos.");
        resposta.put(
                "erros",
                exception.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .collect(
                                java.util.stream.Collectors.toMap(
                                        erro -> erro.getField(),
                                        erro -> erro.getDefaultMessage(),
                                        (atual, anterior) -> atual
                                )
                        )
        );

        return ResponseEntity.badRequest().body(resposta);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> tratarViolacaoDeRestricao(
            ConstraintViolationException exception) {

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("status", 400);
        resposta.put("mensagem", "Os parâmetros enviados são inválidos.");
        resposta.put(
                "erros",
                exception.getConstraintViolations()
                        .stream()
                        .map(violacao -> violacao.getMessage())
                        .toList()
        );

        return ResponseEntity.badRequest().body(resposta);
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<Map<String, Object>> tratarFalhaDeIntegracao(
            ExternalApiException exception) {

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("status", exception.getStatus());
        resposta.put("provedor", exception.getProvider());
        resposta.put("mensagem", exception.getMessage());

        return ResponseEntity
                .status(exception.getStatus())
                .body(resposta);
    }

    private boolean mensagemTecnica(String mensagem) {
        if (mensagem == null || mensagem.isBlank()) {
            return false;
        }

        String normalizada = mensagem.toLowerCase(java.util.Locale.ROOT);
        return normalizada.contains("jdbc")
                || normalizada.contains("hibernate")
                || normalizada.contains("sql")
                || normalizada.contains("column ")
                || normalizada.contains("relation ")
                || normalizada.contains("could not execute")
                || normalizada.contains("constraint ");
    }
}
