package pe.edu.perumar.perumar_backend.dto;

import java.time.Instant;
import java.util.List;

public class CarreraResponse {
  private String codigo;
  private String nombre;
  private String descripcion;
  private String modalidad;     // "CON_EXPERIENCIA" | "SIN_EXPERIENCIA"
  private List<String> materias;
  private String estado;        // "ACTIVO" | "INACTIVO"
  private Instant createdAt;
  private Instant updatedAt;

  public String getCodigo() { return codigo; }
  public void setCodigo(String codigo) { this.codigo = codigo; }

  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }

  public String getDescripcion() { return descripcion; }
  public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

  public String getModalidad() { return modalidad; }
  public void setModalidad(String modalidad) { this.modalidad = modalidad; }

  public List<String> getMaterias() { return materias; }
  public void setMaterias(List<String> materias) { this.materias = materias; }

  public String getEstado() { return estado; }
  public void setEstado(String estado) { this.estado = estado; }

  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

  public Instant getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
