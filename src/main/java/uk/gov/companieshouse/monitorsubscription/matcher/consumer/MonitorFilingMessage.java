package uk.gov.companieshouse.monitorsubscription.matcher.consumer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MonitorFilingMessage {

    @JsonProperty("company_number")
    private String companyNumber;

    @JsonProperty("version")
    private String version;

    @JsonProperty("offset")
    private String offset;

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

}
