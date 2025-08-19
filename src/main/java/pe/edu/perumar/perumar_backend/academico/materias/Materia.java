package pe.edu.perumar.perumar_backend.academico.materias;

import java.time.Instant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class Materia {
  private String codigo;        // PK
  private String nombre;
  private String descripcion;
  private String estado;        // ACTIVO | INACTIVO
  private Instant createdAt;
  private Instant updatedAt;

  @DynamoDbPartitionKey
  @Pattern(regexp = "^[A-Z0-9_-]{3,12}$")
  public String getCodigo() { return codigo; }
  public void setCodigo(String codigo) { this.codigo = codigo; }

  @NotBlank @Size(min = 3, max = 80)
  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }

  @Size(max = 250)
  public String getDescripcion() { return descripcion; }
  public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

  @Pattern(regexp = "^(ACTIVO|INACTIVO)$")
  public String getEstado() { return estado; }
  public void setEstado(String estado) { this.estado = estado; }

  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

  public Instant getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
