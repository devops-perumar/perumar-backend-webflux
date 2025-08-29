package pe.edu.perumar.perumar_backend.ui.model;

import java.util.List;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class UiMenuConfigEntity {
    private String pk; // "ROLE#DIRECTOR"
    private String sk; // "MENU#v1"
    private String role;                    // PK: DIRECTOR | COORDINADOR | ADMIN
    private java.util.List<UiMenuSection> sections;
    private String updatedAt;               // ISO 8601 (opcional)

    @DynamoDbPartitionKey
    @software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute("PK")
    public String getPk() { return pk; }
    public void setPk(String pk) { this.pk = pk; }

    @DynamoDbSortKey
    @software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute("SK")
    public String getSk() { return sk; }
    public void setSk(String sk) { this.sk = sk; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public List<UiMenuSection> getSections() { return sections; }
    public void setSections(List<UiMenuSection> sections) { this.sections = sections; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
