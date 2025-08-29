// src/main/java/pe/edu/perumar/perumar_backend/audit/MenuClickRequest.java
package pe.edu.perumar.perumar_backend.audit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MenuClickRequest(
        // opcional: si no viene, lo fijamos en server
        Long ts,
        @NotBlank @Size(max = 60) String section,
        @NotBlank @Size(max = 60) String item,
        @NotBlank @Size(max = 120)
        @Pattern(regexp = "^[\\w\\-\\/\\?=&#.%]*$") // OWASP: evita payload raro en path
        String path
) {}
