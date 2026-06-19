package com.covas.pipeline;

import com.covas.exception.TtsException;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class RetryPolicyTest {

    private final RetryPolicy policy = RetryPolicy.forTesting();

    @Test
    void succeedsOnFirstAttempt() {
        String result = policy.execute(() -> "ok");
        assertEquals("ok", result);
    }

    @Test
    void retriesAndSucceedsOnSecondAttempt() {
        AtomicInteger attempts = new AtomicInteger(0);
        String result = policy.execute(() -> {
            if (attempts.incrementAndGet() < 2) throw new RuntimeException("falhou");
            return "ok";
        });
        assertEquals("ok", result);
        assertEquals(2, attempts.get());
    }

    @Test
    void throwsExactOriginalExceptionAfterMaxAttempts() {
        TtsException original = new TtsException("limite", 429);
        TtsException thrown = assertThrows(TtsException.class, () ->
                policy.execute(() -> { throw original; })
        );
        assertSame(original, thrown);
    }

    @Test
    void exhaustsAllAttemptsBeforeThrowing() {
        AtomicInteger attempts = new AtomicInteger(0);
        assertThrows(RuntimeException.class, () ->
                policy.execute(() -> {
                    attempts.incrementAndGet();
                    throw new RuntimeException("sempre falha");
                })
        );
        assertEquals(3, attempts.get());
    }

    @Test
    void returnsNullWhenOperationReturnsNull() {
        Object result = policy.execute(() -> null);
        assertNull(result);
    }
}
