package br.com.locus.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "RUN_REDIS_TESTS", matches = "true")
class RedisIntegracaoTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void deveSalvarERecuperarValorNoRedis() {
        String chave = "locus:teste";
        String valor = "redis-funcionando";

        redisTemplate.opsForValue().set(chave, valor);

        String valorRecuperado =
                redisTemplate.opsForValue().get(chave);

        assertEquals(valor, valorRecuperado);

        redisTemplate.delete(chave);
    }
}
