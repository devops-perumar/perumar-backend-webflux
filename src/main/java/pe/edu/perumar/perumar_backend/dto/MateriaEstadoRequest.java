package pe.edu.perumar.perumar_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class MateriaEstadoRequest {
  @NotBlank(message = "estado requerido")
  @Pattern(regexp = "^(ACTIVO|INACTIVO)$", message = "estado debe ser ACTIVO o INACTIVO")
  private String estado;

  public String getEstado() { return estado; }
  public void setEstado(String estado) { this.estado = estado; }
}
