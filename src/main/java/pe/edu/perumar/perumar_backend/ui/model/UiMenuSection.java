package pe.edu.perumar.perumar_backend.ui.model;

import java.util.List;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@DynamoDbBean
public class UiMenuSection {
    private String label;
    private java.util.List<UiMenuItem> items;

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public List<UiMenuItem> getItems() { return items; }
    public void setItems(List<UiMenuItem> items) { this.items = items; }
}
