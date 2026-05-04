package com.cg.frontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeDto {

    private String empId;
    private String fname;
    private String minit;
    private String lname;
    private Short jobId;
    private Integer jobLvl;
    private String pubId;
    private String hireDate;

    public EmployeeDto() {}

    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }

    public String getFname() { return fname; }
    public void setFname(String fname) { this.fname = fname; }

    public String getMinit() { return minit; }
    public void setMinit(String minit) { this.minit = minit; }

    public String getLname() { return lname; }
    public void setLname(String lname) { this.lname = lname; }

    public Short getJobId() { return jobId; }
    public void setJobId(Short jobId) { this.jobId = jobId; }

    public Integer getJobLvl() { return jobLvl; }
    public void setJobLvl(Integer jobLvl) { this.jobLvl = jobLvl; }

    public String getPubId() { return pubId; }
    public void setPubId(String pubId) { this.pubId = pubId; }

    public String getHireDate() { return hireDate; }
    public void setHireDate(String hireDate) { this.hireDate = hireDate; }

    @JsonIgnore
    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        if (fname != null) sb.append(fname);
        if (minit != null && !minit.isBlank()) sb.append(" ").append(minit).append(".");
        if (lname != null) sb.append(" ").append(lname);
        return sb.toString().trim();
    }

    @JsonIgnore
    public String getInitials() {
        String f = (fname != null && !fname.isBlank()) ? fname.substring(0, 1).toUpperCase() : "";
        String l = (lname != null && !lname.isBlank()) ? lname.substring(0, 1).toUpperCase() : "";
        return f + l;
    }
}
