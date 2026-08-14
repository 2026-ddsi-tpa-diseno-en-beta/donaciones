package ar.edu.utn.dds.k3003.repositories;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryGeneradorDeIds implements GeneradorDeIds {
  private final Map<String, AtomicLong> contadores = new ConcurrentHashMap<>();

  @Override
  public String siguiente(String secuencia) {
    return String.valueOf(
        contadores.computeIfAbsent(secuencia, s -> new AtomicLong(0)).incrementAndGet());
  }
}
