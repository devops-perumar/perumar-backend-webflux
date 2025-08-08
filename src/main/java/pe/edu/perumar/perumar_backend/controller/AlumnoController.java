package pe.edu.perumar.perumar_backend.controller;

import pe.edu.perumar.perumar_backend.model.Alumno;
import pe.edu.perumar.perumar_backend.repository.AlumnoRepository;
import pe.edu.perumar.perumar_backend.service.S3SignedUrlService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import java.time.Duration;
import java.util.UUID;

import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;



@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/v1/alumnos")
public class AlumnoController {

    private final AlumnoRepository alumnoRepository;
    private final S3AsyncClient s3AsyncClient;
    private final String bucketName;
    private final String region;
    private final S3Presigner s3Presigner;

    public AlumnoController(AlumnoRepository alumnoRepository,
                            S3AsyncClient s3AsyncClient,
                            S3Presigner s3Presigner, // <--- Agregado aquí
                            @Value("${aws.s3.bucket}") String bucketName,
                            @Value("${aws.region}") String region,
                            S3SignedUrlService s3SignedUrlService) {
        this.alumnoRepository = alumnoRepository;
        this.s3AsyncClient = s3AsyncClient;
        this.bucketName = bucketName;
        this.region = region;
        this.s3Presigner = s3Presigner;
    }

    /**
     * Elimina todos los objetos en S3 que tengan un prefijo específico.
     * @param prefix El prefijo de los objetos a eliminar.
     * @return Mono<Void> que completa cuando todos los objetos han sido eliminados.
     */
    public Mono<Void> deleteAllS3ObjectsByPrefix(String prefix) {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build();

        // Función recursiva para manejar paginación
        return deleteS3ObjectsPaginated(request);
    }

    /**
     * Elimina todos los objetos en S3 de forma paginada.
     * @param request La solicitud para listar objetos en S3.
     * @return Mono<Void> que completa cuando todos los objetos han sido eliminados.
     */
    private Mono<Void> deleteS3ObjectsPaginated(ListObjectsV2Request request) {
        return Mono.fromFuture(s3AsyncClient.listObjectsV2(request))
                .flatMapMany(response -> {
                    // Borra todos los objetos de esta página
                    Flux<Void> deleteFlux = Flux.fromIterable(response.contents())
                        .flatMap(obj -> Mono.fromFuture(
                            s3AsyncClient.deleteObject(builder -> builder.bucket(bucketName).key(obj.key()).build())
                        ).then());
                    // Si hay más páginas, llama recursivamente
                    if (response.isTruncated()) {
                        ListObjectsV2Request nextRequest = request.toBuilder()
                            .continuationToken(response.nextContinuationToken())
                            .build();
                        return deleteFlux.concatWith(deleteS3ObjectsPaginated(nextRequest));
                    }
                    return deleteFlux;
                })
                .then();
    }

    // CRUD operations for Alumno
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Alumno> create(@RequestBody Alumno alumno) {
        if (alumno.getAlumnoId() == null || alumno.getAlumnoId().isEmpty()) {
            alumno.setAlumnoId(UUID.randomUUID().toString()); // o tu lógica de generación
        }
        return alumnoRepository.save(alumno);
    }

    @GetMapping("/{id}")
    public Mono<Alumno> getById(@PathVariable String id) {
        return alumnoRepository.findById(id);
    }

    @GetMapping
    public Flux<Alumno> getAll() {
        return alumnoRepository.findAll();
    }

    @PutMapping("/{id}")
    public Mono<Alumno> update(@PathVariable String id, @RequestBody Alumno alumno) {
        alumno.setAlumnoId(id);
        return alumnoRepository.save(alumno);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        String prefix = "alumnos/" + id + "/";
        return deleteAllS3ObjectsByPrefix(prefix)
                .then(alumnoRepository.deleteById(id));
    }

    @SuppressWarnings("null")
    @PostMapping("/{id}/foto")
    public Mono<String> subirFoto(@PathVariable String id, @RequestPart("file") FilePart filePart) {
        String key = "alumnos/" + id + "/" + UUID.randomUUID() + "-" + filePart.filename();

        // Subir a S3
        return filePart.content()
                .reduce(new byte[0], (prev, dataBuffer) -> {
                    byte[] current = new byte[prev.length + dataBuffer.readableByteCount()];
                    System.arraycopy(prev, 0, current, 0, prev.length);
                    dataBuffer.read(current, prev.length, dataBuffer.readableByteCount());
                    return current;
                })
                .flatMap((byte[] bytes) -> Mono.fromFuture(
                        s3AsyncClient.putObject(
                                PutObjectRequest.builder()
                                        .bucket(bucketName)
                                        .key(key)
                                        .contentType(
                                            filePart.headers().getContentType() != null
                                                ? filePart.headers().getContentType().toString()
                                                : "application/octet-stream"
                                        )
                                        .build(),
                                software.amazon.awssdk.core.async.AsyncRequestBody.fromBytes(bytes)
                        )
                ).thenReturn(key))
                .flatMap(s3Key -> {
                    String s3Url = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + s3Key;
                    return alumnoRepository.findById(id)
                            .doOnNext(alumno -> System.out.println("Alumno encontrado: " + alumno))
                            .flatMap(alumno -> {
                                alumno.setFotoUrl(s3Url);
                                System.out.println("Actualizando fotoUrl a: " + s3Url);
                                return alumnoRepository.save(alumno)
                                        .doOnNext(updated -> System.out.println("Alumno actualizado: " + updated))
                                        .thenReturn(s3Url);
                            });
                });
    }

    // obtener la URL firmada para la foto
    @GetMapping("/{id}/foto-url")
    public Mono<String> getFotoSignedUrl(@PathVariable String id) {
        return alumnoRepository.findById(id)
            .flatMap(alumno -> {
                if (alumno.getFotoUrl() == null) return Mono.just("");
                // Extrae el S3 key desde la URL guardada
                String key = alumno.getFotoUrl().replaceFirst("^https://[^/]+/([^?]+).*", "$1");
                
                // Aquí usas el s3Presigner directamente
                GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
                GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(2))
                    .getObjectRequest(getObjectRequest)
                    .build();
                String signedUrl = s3Presigner.presignGetObject(presignRequest).url().toString();
                return Mono.just(signedUrl);
            });
    }
}
