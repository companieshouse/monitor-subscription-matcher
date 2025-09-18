package uk.gov.companieshouse.monitorsubscription.matcher.producer.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "type",
        "description",
        "description_values",
        "links",
        "category",
        "paper_filed",
        "subcategory",
        "action_date",
        "date"
})
public class Data {

    @JsonProperty("type")
    private String type;

    @JsonProperty("description")
    private String description;

    @JsonProperty("description_values")
    private DescriptionValues descriptionValues;

    @JsonProperty("links")
    private Links links;

    @JsonProperty("category")
    private String category;

    @JsonProperty("paper_filed")
    private Boolean paperFiled;

    @JsonProperty("subcategory")
    private String subcategory;

    @JsonProperty("action_date")
    private String actionDate;

    @JsonProperty("date")
    private String date;

    @JsonIgnore
    private final Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("description_values")
    public DescriptionValues getDescriptionValues() {
        return descriptionValues;
    }

    @JsonProperty("description_values")
    public void setDescriptionValues(DescriptionValues descriptionValues) {
        this.descriptionValues = descriptionValues;
    }

    @JsonProperty("links")
    public Links getLinks() {
        return links;
    }

    @JsonProperty("links")
    public void setLinks(Links links) {
        this.links = links;
    }

    @JsonProperty("category")
    public String getCategory() {
        return category;
    }

    @JsonProperty("category")
    public void setCategory(String category) {
        this.category = category;
    }

    @JsonProperty("paper_filed")
    public Boolean getPaperFiled() {
        return paperFiled;
    }

    @JsonProperty("paper_filed")
    public void setPaperFiled(Boolean paperFiled) {
        this.paperFiled = paperFiled;
    }

    @JsonProperty("subcategory")
    public String getSubcategory() {
        return subcategory;
    }

    @JsonProperty("subcategory")
    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    @JsonProperty("action_date")
    public String getActionDate() {
        return actionDate;
    }

    @JsonProperty("action_date")
    public void setActionDate(String actionDate) {
        this.actionDate = actionDate;
    }

    @JsonProperty("date")
    public String getDate() {
        return date;
    }

    @JsonProperty("date")
    public void setDate(String date) {
        this.date = date;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}