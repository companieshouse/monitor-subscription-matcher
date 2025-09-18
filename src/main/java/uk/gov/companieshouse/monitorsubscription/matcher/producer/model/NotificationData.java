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
        "app_id",
        "company_number",
        "data",
        "is_delete"
})
public class NotificationData {

    @JsonProperty("app_id")
    private String appId;

    @JsonProperty("company_number")
    private String companyNumber;

    @JsonProperty("data")
    private Data data;

    @JsonProperty("is_delete")
    private Boolean isDelete;

    @JsonIgnore
    private final Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("app_id")
    public String getAppId() {
        return appId;
    }

    @JsonProperty("app_id")
    public void setAppId(String appId) {
        this.appId = appId;
    }

    @JsonProperty("company_number")
    public String getCompanyNumber() {
        return companyNumber;
    }

    @JsonProperty("company_number")
    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    @JsonProperty("data")
    public Data getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(Data data) {
        this.data = data;
    }

    @JsonProperty("is_delete")
    public Boolean getIsDelete() {
        return isDelete;
    }

    @JsonProperty("is_delete")
    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
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