// src/main/java/pe/edu/perumar/perumar_backend/acl/AccessControlItem.java
package pe.edu.perumar.perumar_backend.acl;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

// AccessControlItem.java
@DynamoDbBean
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AccessControlItem {
  private String role;                  // PK
  private String resourceActionScope;   // SK (ej: "materias:ver", "academico:*")
  private boolean allow;                // true si se permite, false si se deniega

  @DynamoDbPartitionKey
  @DynamoDbAttribute("role")
  public String getRole() { return role; }
  public void setRole(String role) { this.role = role; }

  @DynamoDbSortKey
  @DynamoDbAttribute("resource_action_scope")
  public String getResourceActionScope() { return resourceActionScope; }
  public void setResourceActionScope(String v) { this.resourceActionScope = v; }

  @DynamoDbAttribute("allow")
  public boolean getAllow() { return allow; }
  public void setAllow(boolean allow) { this.allow = allow; }
}

