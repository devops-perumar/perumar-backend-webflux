package pe.edu.perumar.perumar_backend.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record CicloRequest(
  @NotBlank String codigoCarrera,
  @NotNull LocalDate fechaInicio,
  @NotNull LocalDate fechaFin,
  List<String> materias,
  String promocion,
  Map<String,String> ubicacion,
  String nombreCiclo // opcional; si null, se autogenera
) {}
