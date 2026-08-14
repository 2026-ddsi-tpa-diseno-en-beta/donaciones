package ar.edu.utn.dds.k3003.exceptions;

/**
 * La donacion no puede aceptarse por una regla de negocio: el donador no existe, esta baneado, o
 * el sorteo del 50% de un donador sospechoso salio negativo.
 */
public class DonacionRechazadaException extends RuntimeException {
  public DonacionRechazadaException(String mensaje) {
    super(mensaje);
  }
}
