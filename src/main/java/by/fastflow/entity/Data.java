package by.fastflow.entity;

import by.fastflow.utils.DomainObject;

import java.util.UUID;

/**
 * Created by KuSu on 01.07.2016.
 */
public class Data implements DomainObject {

    private UUID id;
    private String description;

    public Data(UUID id, String description) {
        this.id = id;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}