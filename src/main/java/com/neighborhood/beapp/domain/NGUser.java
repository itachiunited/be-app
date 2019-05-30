package com.neighborhood.beapp.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

import com.neighborhood.beapp.domain.enumeration.VegetationType;

import com.neighborhood.beapp.domain.enumeration.Status;

/**
 * A NGUser.
 */
@Document(collection = "ng_user")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "nguser")
public class NGUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private String id;

    @NotNull
    @Field("phone_number")
    private String phoneNumber;

    @Field("first_name")
    private String firstName;

    @Field("last_name")
    private String lastName;

    @Field("vegetation_type")
    private VegetationType vegetationType;

    @Field("garden_description")
    private String gardenDescription;

    @Field("email")
    private String email;

    @Field("status")
    private Status status;

    @Field("one_time_code")
    private String oneTimeCode;

    @Field("one_time_expiration_time")
    private Instant oneTimeExpirationTime;

    @DBRef
    @Field("location")
    private Location location;

    @DBRef
    @Field("devices")
    private Set<DeviceDetails> devices = new HashSet<>();

    @DBRef
    @Field("gardenImage")
    private Set<Images> gardenImages = new HashSet<>();

    @DBRef
    @Field("posts")
    private Set<Post> posts = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public NGUser phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public NGUser firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public NGUser lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public VegetationType getVegetationType() {
        return vegetationType;
    }

    public NGUser vegetationType(VegetationType vegetationType) {
        this.vegetationType = vegetationType;
        return this;
    }

    public void setVegetationType(VegetationType vegetationType) {
        this.vegetationType = vegetationType;
    }

    public String getGardenDescription() {
        return gardenDescription;
    }

    public NGUser gardenDescription(String gardenDescription) {
        this.gardenDescription = gardenDescription;
        return this;
    }

    public void setGardenDescription(String gardenDescription) {
        this.gardenDescription = gardenDescription;
    }

    public String getEmail() {
        return email;
    }

    public NGUser email(String email) {
        this.email = email;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Status getStatus() {
        return status;
    }

    public NGUser status(Status status) {
        this.status = status;
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getOneTimeCode() {
        return oneTimeCode;
    }

    public NGUser oneTimeCode(String oneTimeCode) {
        this.oneTimeCode = oneTimeCode;
        return this;
    }

    public void setOneTimeCode(String oneTimeCode) {
        this.oneTimeCode = oneTimeCode;
    }

    public Instant getOneTimeExpirationTime() {
        return oneTimeExpirationTime;
    }

    public NGUser oneTimeExpirationTime(Instant oneTimeExpirationTime) {
        this.oneTimeExpirationTime = oneTimeExpirationTime;
        return this;
    }

    public void setOneTimeExpirationTime(Instant oneTimeExpirationTime) {
        this.oneTimeExpirationTime = oneTimeExpirationTime;
    }

    public Location getLocation() {
        return location;
    }

    public NGUser location(Location location) {
        this.location = location;
        return this;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Set<DeviceDetails> getDevices() {
        return devices;
    }

    public NGUser devices(Set<DeviceDetails> deviceDetails) {
        this.devices = deviceDetails;
        return this;
    }

    public NGUser addDevices(DeviceDetails deviceDetails) {
        this.devices.add(deviceDetails);
        deviceDetails.setNGUser(this);
        return this;
    }

    public NGUser removeDevices(DeviceDetails deviceDetails) {
        this.devices.remove(deviceDetails);
        deviceDetails.setNGUser(null);
        return this;
    }

    public void setDevices(Set<DeviceDetails> deviceDetails) {
        this.devices = deviceDetails;
    }

    public Set<Images> getGardenImages() {
        return gardenImages;
    }

    public NGUser gardenImages(Set<Images> images) {
        this.gardenImages = images;
        return this;
    }

    public NGUser addGardenImage(Images images) {
        this.gardenImages.add(images);
        images.setNGUser(this);
        return this;
    }

    public NGUser removeGardenImage(Images images) {
        this.gardenImages.remove(images);
        images.setNGUser(null);
        return this;
    }

    public void setGardenImages(Set<Images> images) {
        this.gardenImages = images;
    }

    public Set<Post> getPosts() {
        return posts;
    }

    public NGUser posts(Set<Post> posts) {
        this.posts = posts;
        return this;
    }

    public NGUser addPosts(Post post) {
        this.posts.add(post);
        post.setNGUser(this);
        return this;
    }

    public NGUser removePosts(Post post) {
        this.posts.remove(post);
        post.setNGUser(null);
        return this;
    }

    public void setPosts(Set<Post> posts) {
        this.posts = posts;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NGUser)) {
            return false;
        }
        return id != null && id.equals(((NGUser) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "NGUser{" +
            "id=" + getId() +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", vegetationType='" + getVegetationType() + "'" +
            ", gardenDescription='" + getGardenDescription() + "'" +
            ", email='" + getEmail() + "'" +
            ", status='" + getStatus() + "'" +
            ", oneTimeCode='" + getOneTimeCode() + "'" +
            ", oneTimeExpirationTime='" + getOneTimeExpirationTime() + "'" +
            "}";
    }
}
