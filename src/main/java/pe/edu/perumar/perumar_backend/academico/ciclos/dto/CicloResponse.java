package pe.edu.perumar.perumar_backend.academico.ciclos.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record CicloResponse(
  String id,
  String codigoCarrera,
  String nombreCiclo,
  LocalDate fechaInicio,
  LocalDate fechaFin,
  List<String> materias,
  String promocion,
  Map<String,String> ubicacion,
  String estado,
  Instant createdAt,
  Instant updatedAt
) {}
