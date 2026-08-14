package ar.edu.utn.dds.k3003.repositories;

/** Asigna los identificadores numericos de las entidades de las que este modulo es duenio. */
public interface GeneradorDeIds {
  String siguiente(String secuencia);
}
