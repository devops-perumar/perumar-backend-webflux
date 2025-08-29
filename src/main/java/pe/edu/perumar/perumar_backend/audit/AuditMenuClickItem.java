// src/main/java/pe/edu/perumar/perumar_backend/audit/AuditMenuClickItem.java
package pe.edu.perumar.perumar_backend.audit;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditMenuClickItem {
    private String pk;           // user#<sub>
    private String sk;           // ts#<epoch>#<uuid>
    private String role;         // rol principal (primer group)
    private String section;
    private String item;
    private String path;
    private Long ts;
    private String ip;
    private String userAgent;
    private Long ttl;           // epoch seconds (90 días desde ahora)

    @DynamoDbPartitionKey @DynamoDbAttribute("pk")
    public String getPk() { return pk; }
    public void setPk(String v) { this.pk = v; }

    @DynamoDbSortKey @DynamoDbAttribute("sk")
    public String getSk() { return sk; }
    public void setSk(String v) { this.sk = v; }

    @DynamoDbAttribute("role")
    public String getRole() { return role; }
    public void setRole(String v) { this.role = v; }

    @DynamoDbAttribute("section")
    public String getSection() { return section; }
    public void setSection(String v) { this.section = v; }

    @DynamoDbAttribute("item")
    public String getItem() { return item; }
    public void setItem(String v) { this.item = v; }

    @DynamoDbAttribute("path")
    public String getPath() { return path; }
    public void setPath(String v) { this.path = v; }

    @DynamoDbAttribute("ts")
    public Long getTs() { return ts; }
    public void setTs(Long v) { this.ts = v; }

    @DynamoDbAttribute("ip")
    public String getIp() { return ip; }
    public void setIp(String v) { this.ip = v; }

    @DynamoDbAttribute("user_agent")
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String v) { this.userAgent = v; }

    @DynamoDbAttribute("ttl")
    public Long getTtl() { return ttl; }
    
}
