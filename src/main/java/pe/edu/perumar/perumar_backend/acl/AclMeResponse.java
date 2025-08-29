// src/main/java/pe/edu/perumar/perumar_backend/acl/AclMeResponse.java
package pe.edu.perumar.perumar_backend.acl;

import java.util.Set;

public record AclMeResponse(Set<String> perms) {}
