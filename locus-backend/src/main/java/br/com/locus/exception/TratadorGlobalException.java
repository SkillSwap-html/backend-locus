package br.com.locus.exception;

import br.com.locus.integracao.ExternalApiException;
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

        resposta.put("status", 400);
        resposta.put("mensagem", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
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
}
