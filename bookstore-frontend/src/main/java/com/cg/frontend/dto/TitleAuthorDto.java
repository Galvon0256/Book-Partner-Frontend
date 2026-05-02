package com.cg.frontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TitleAuthorDto {

    private String auId;
    private String titleId;
    private Integer auOrd;
    private Integer royaltyper;
    private String titleName;
    private String titleType;

    public TitleAuthorDto() {}

    public String getAuId() { return auId; }
    public void setAuId(String auId) { this.auId = auId; }

    public String getTitleId() { return titleId; }
    public void setTitleId(String titleId) { this.titleId = titleId; }

    public Integer getAuOrd() { return auOrd; }
    public void setAuOrd(Integer auOrd) { this.auOrd = auOrd; }

    public Integer getRoyaltyper() { return royaltyper; }
    public void setRoyaltyper(Integer royaltyper) { this.royaltyper = royaltyper; }

    public String getTitleName() { return titleName; }
    public void setTitleName(String titleName) { this.titleName = titleName; }

    public String getTitleType() { return titleType; }
    public void setTitleType(String titleType) { this.titleType = titleType; }
}
