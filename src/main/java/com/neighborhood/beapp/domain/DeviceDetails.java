package com.neighborhood.beapp.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DeviceDetails.
 */
@Document(collection = "device_details")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "devicedetails")
public class DeviceDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private String id;

    @NotNull
    @Field("device_id")
    private String deviceId;

    @DBRef
    @Field("nGUser")
    @JsonIgnoreProperties("deviceDetails")
    private NGUser nGUser;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public DeviceDetails deviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public NGUser getNGUser() {
        return nGUser;
    }

    public DeviceDetails nGUser(NGUser nGUser) {
        this.nGUser = nGUser;
        return this;
    }

    public void setNGUser(NGUser nGUser) {
        this.nGUser = nGUser;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeviceDetails)) {
            return false;
        }
        return id != null && id.equals(((DeviceDetails) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "DeviceDetails{" +
            "id=" + getId() +
            ", deviceId='" + getDeviceId() + "'" +
            "}";
    }
}
