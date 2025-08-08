package pe.edu.perumar.perumar_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class Alumno {

    private String alumnoId;
    private String nombres;
    private String apellidos;
    private String correo;
    private String fotoUrl;

    @DynamoDbPartitionKey
    public String getAlumnoId() {
        return alumnoId;
    }
}
