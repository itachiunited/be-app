package com.neighborhood.beapp.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Post.
 */
@Document(collection = "post")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "post")
public class Post implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private String id;

    @Field("title")
    private String title;

    @Field("price")
    private Float price;

    @Field("msrp")
    private Float msrp;

    @Field("weight")
    private Float weight;

    @Field("available_qty")
    private Integer availableQty;

    @Field("remaining_qty")
    private Integer remainingQty;

    @Field("description")
    private String description;

    @Field("more_info")
    private String moreInfo;

    @Field("start_time")
    private Instant startTime;

    @Field("end_time")
    private Instant endTime;

    @DBRef
    @Field("nGUser")
    @JsonIgnoreProperties("posts")
    private NGUser nGUser;

    @DBRef
    @Field("images")
    private Set<Images> images = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public Post title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Float getPrice() {
        return price;
    }

    public Post price(Float price) {
        this.price = price;
        return this;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getMsrp() {
        return msrp;
    }

    public Post msrp(Float msrp) {
        this.msrp = msrp;
        return this;
    }

    public void setMsrp(Float msrp) {
        this.msrp = msrp;
    }

    public Float getWeight() {
        return weight;
    }

    public Post weight(Float weight) {
        this.weight = weight;
        return this;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Integer getAvailableQty() {
        return availableQty;
    }

    public Post availableQty(Integer availableQty) {
        this.availableQty = availableQty;
        return this;
    }

    public void setAvailableQty(Integer availableQty) {
        this.availableQty = availableQty;
    }

    public Integer getRemainingQty() {
        return remainingQty;
    }

    public Post remainingQty(Integer remainingQty) {
        this.remainingQty = remainingQty;
        return this;
    }

    public void setRemainingQty(Integer remainingQty) {
        this.remainingQty = remainingQty;
    }

    public String getDescription() {
        return description;
    }

    public Post description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMoreInfo() {
        return moreInfo;
    }

    public Post moreInfo(String moreInfo) {
        this.moreInfo = moreInfo;
        return this;
    }

    public void setMoreInfo(String moreInfo) {
        this.moreInfo = moreInfo;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Post startTime(Instant startTime) {
        this.startTime = startTime;
        return this;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public Post endTime(Instant endTime) {
        this.endTime = endTime;
        return this;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public NGUser getNGUser() {
        return nGUser;
    }

    public Post nGUser(NGUser nGUser) {
        this.nGUser = nGUser;
        return this;
    }

    public void setNGUser(NGUser nGUser) {
        this.nGUser = nGUser;
    }

    public Set<Images> getImages() {
        return images;
    }

    public Post images(Set<Images> images) {
        this.images = images;
        return this;
    }

    public Post addImages(Images images) {
        this.images.add(images);
        images.setPost(this);
        return this;
    }

    public Post removeImages(Images images) {
        this.images.remove(images);
        images.setPost(null);
        return this;
    }

    public void setImages(Set<Images> images) {
        this.images = images;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Post)) {
            return false;
        }
        return id != null && id.equals(((Post) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Post{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", price=" + getPrice() +
            ", msrp=" + getMsrp() +
            ", weight=" + getWeight() +
            ", availableQty=" + getAvailableQty() +
            ", remainingQty=" + getRemainingQty() +
            ", description='" + getDescription() + "'" +
            ", moreInfo='" + getMoreInfo() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            "}";
    }
}
