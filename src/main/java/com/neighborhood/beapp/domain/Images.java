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

import com.neighborhood.beapp.domain.enumeration.ImageType;

/**
 * A Images.
 */
@Document(collection = "images")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "images")
public class Images implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private String id;

    @NotNull
    @Field("image_url")
    private String imageURL;

    @NotNull
    @Field("image_type")
    private ImageType imageType;

    @DBRef
    @Field("nGUser")
    @JsonIgnoreProperties("images")
    private NGUser nGUser;

    @DBRef
    @Field("post")
    @JsonIgnoreProperties("images")
    private Post post;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public Images imageURL(String imageURL) {
        this.imageURL = imageURL;
        return this;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public ImageType getImageType() {
        return imageType;
    }

    public Images imageType(ImageType imageType) {
        this.imageType = imageType;
        return this;
    }

    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }

    public NGUser getNGUser() {
        return nGUser;
    }

    public Images nGUser(NGUser nGUser) {
        this.nGUser = nGUser;
        return this;
    }

    public void setNGUser(NGUser nGUser) {
        this.nGUser = nGUser;
    }

    public Post getPost() {
        return post;
    }

    public Images post(Post post) {
        this.post = post;
        return this;
    }

    public void setPost(Post post) {
        this.post = post;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Images)) {
            return false;
        }
        return id != null && id.equals(((Images) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Images{" +
            "id=" + getId() +
            ", imageURL='" + getImageURL() + "'" +
            ", imageType='" + getImageType() + "'" +
            "}";
    }
}
