package pe.edu.perumar.perumar_backend.academico.carreras.dto;

import jakarta.validation.constraints.*;
import pe.edu.perumar.perumar_backend.academico.carreras.model.ModalidadCarrera;

import java.util.List;

public class CarreraRequest {

    @NotBlank(message = "codigo requerido")
    @Pattern(regexp = "^[A-Za-z0-9_-]{3,12}$", message = "codigo alfanumérico 3–12 (guion y guion_bajo permitidos)")
    private String codigo;

    @NotBlank(message = "nombre requerido")
    @Size(min = 3, max = 80, message = "nombre 3–80 caracteres")
    private String nombre;

    @Size(max = 300, message = "descripcion hasta 300 caracteres")
    private String descripcion;

    @NotNull(message = "modalidad requerida")
    private ModalidadCarrera modalidad; // CON_EXPERIENCIA | SIN_EXPERIENCIA

    @NotNull(message = "materias requeridas (puede ser vacía)")
    private List<
        @Pattern(regexp = "^[A-Za-z0-9_-]{3,12}$", message = "codigo de materia inválido")
        String
    > materias;

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public ModalidadCarrera getModalidad() { return modalidad; }
    public void setModalidad(ModalidadCarrera modalidad) { this.modalidad = modalidad; }

    public List<String> getMaterias() { return materias; }
    public void setMaterias(List<String> materias) { this.materias = materias; }
}
