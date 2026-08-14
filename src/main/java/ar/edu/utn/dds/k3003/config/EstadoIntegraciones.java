package ar.edu.utn.dds.k3003.config;

import java.util.Map;

/**
 * Que modo de integracion quedo activo para cada componente. Se expone por HTTP para poder
 * diagnosticar en la demo si el modulo esta hablando de verdad con sus vecinos o si quedo con las
 * fachadas locales de prueba.
 */
public record EstadoIntegraciones(Map<String, String> componentes) {

  public boolean todasReales() {
    return componentes.values().stream().noneMatch(modo -> modo.startsWith("LOCAL"));
  }
}
