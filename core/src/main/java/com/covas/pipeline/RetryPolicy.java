package com.covas.pipeline;

import java.time.Duration;
import java.util.function.Supplier;

public final class RetryPolicy {
    private final int maxAttempts;
    private final Duration initialDelay;

    private RetryPolicy(int maxAttempts, Duration initialDelay) {
        this.maxAttempts = maxAttempts;
        this.initialDelay = initialDelay;
    }

    public static RetryPolicy withDefaults() {
        return new RetryPolicy(3, Duration.ofSeconds(1));
    }

    public <T> T execute(Supplier<T> operation) {
        int attempt = 1;
        long currentDelayMs = initialDelay.toMillis();

        while (true) {
            try {
                return operation.get(); // Executa a operação que lhe foi passada
            } catch (Exception e) {
                if (attempt >= maxAttempts) {
                    throw new RuntimeException("Esgotadas as " + maxAttempts + " tentativas", e);
                }
                
                try {
                    Thread.sleep(currentDelayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread interrompida durante o retry", ie);
                }
                
                // [Linha 38] Desafio: Como calculamos o backoff exponencial para a próxima tentativa?
               currentDelayMs *= 2; // Backoff Exponencial
                attempt++;
            }
        }
    }
}