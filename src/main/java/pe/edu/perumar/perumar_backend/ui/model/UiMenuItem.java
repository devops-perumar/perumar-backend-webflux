package pe.edu.perumar.perumar_backend.ui.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@DynamoDbBean
public class UiMenuItem {
    private String label;
    private String path;
    private String icon;
    private String perm; // permiso requerido (ej. "materia:read")

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getPerm() { return perm; }
    public void setPerm(String perm) { this.perm = perm; }
}
