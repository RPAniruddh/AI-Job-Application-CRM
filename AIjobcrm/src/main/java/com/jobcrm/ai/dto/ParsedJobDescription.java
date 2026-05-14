package com.jobcrm.ai.dto;

import java.util.List;

public class ParsedJobDescription {

    private String company;
    private String roleTitle;
    private String salaryRange;
    private List<String> requiredSkills;
    private List<String> niceToHaveSkills;
    private String summary;

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getRoleTitle() { return roleTitle; }
    public void setRoleTitle(String roleTitle) { this.roleTitle = roleTitle; }

    public String getSalaryRange() { return salaryRange; }
    public void setSalaryRange(String salaryRange) { this.salaryRange = salaryRange; }

    public List<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }

    public List<String> getNiceToHaveSkills() { return niceToHaveSkills; }
    public void setNiceToHaveSkills(List<String> niceToHaveSkills) { this.niceToHaveSkills = niceToHaveSkills; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
}