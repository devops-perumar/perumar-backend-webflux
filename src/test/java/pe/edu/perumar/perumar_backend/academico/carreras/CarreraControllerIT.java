package pe.edu.perumar.perumar_backend.controller;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import pe.edu.perumar.perumar_backend.model.Carrera;
import pe.edu.perumar.perumar_backend.model.Materia;
import pe.edu.perumar.perumar_backend.repository.MateriaRepository;
import pe.edu.perumar.perumar_backend.repository.CarreraRepository;
import pe.edu.perumar.perumar_backend.model.ModalidadCarrera;

public class CarreraControllerIT {

    MateriaRepository materiaRepository;
    CarreraRepository carreraRepository;

    void stubsCarrera() {
        materiaRepository = mock(MateriaRepository.class);
        carreraRepository = mock(CarreraRepository.class);
        // ----- Materia válida usada por el service para validar la carrera -----
        Materia mat1 = new Materia();
        mat1.setCodigo("MAT001");
        mat1.setNombre("Navegación I");
        mat1.setEstado("ACTIVO");

        when(materiaRepository.findByCodigo(eq("MAT001"))).thenReturn(Mono.just(mat1));
        when(materiaRepository.findByCodigo(argThat(c -> !"MAT001".equals(c)))).thenReturn(Mono.empty());
        when(materiaRepository.findByEstado("ACTIVO")).thenReturn(Flux.just(mat1));

        // ----- Carrera existente para GET /api/v1/carreras/CAR001 -----
        Carrera car1 = new Carrera();
        car1.setCodigo("CAR001");
        car1.setNombre("Carrera Test");
        car1.setDescripcion("desc");
        car1.setModalidad(ModalidadCarrera.SIN_EXPERIENCIA);
        car1.setEstado("ACTIVO");

        when(carreraRepository.findByCodigo(eq("CAR001"))).thenReturn(Mono.just(car1));
        when(carreraRepository.findByCodigo(argThat(c -> !"CAR001".equals(c)))).thenReturn(Mono.empty());

        // Listado por defecto vacío (ajusta si tu controller usa findAll())
        when(carreraRepository.findAll(anyString())).thenReturn(Flux.empty());

        // Guardar/actualizar devuelven el mismo objeto
        when(carreraRepository.save(any(Carrera.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        // Si tu repo SÍ tiene update(Carrera), deja esto; si no existe, elimínalo
        try {
            carreraRepository.getClass().getMethod("update", Object.class);
            when(carreraRepository.update(any(Carrera.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        } catch (NoSuchMethodException | SecurityException ignored) { /* tu repo no tiene update(Carrera) */ }

        // Duplicado: simula que ya existe CAR_DUP
        Carrera dup = new Carrera();
        dup.setCodigo("CAR_DUP");
        when(carreraRepository.findByCodigo(eq("CAR_DUP"))).thenReturn(Mono.just(dup));
    }
}
