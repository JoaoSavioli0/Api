package com.condolives.api.dto.serviceline;

import java.util.UUID;

import com.condolives.api.entity.ServiceLine.StepContributor;
import com.condolives.api.entity.User.Company;
import com.condolives.api.entity.User.Staff;

public record StepContributorResponse(
        UUID id,
        String sourceType,
        UUID staffId,
        UUID companyId,
        String name,
        String companyName,
        String role) {

    public static StepContributorResponse from(StepContributor c) {
        String companyName = null;
        if ("externo".equals(c.getType())) {
            Staff staff = c.getStaff();
            if (staff != null) {
                Company cmp = staff.getCompany();
                if (cmp != null) {
                    companyName = cmp.getName();
                }
            }
        }
        return new StepContributorResponse(
                c.getId(),
                c.getType(),
                c.getStaffId(),
                c.getCompanyId(),
                c.getName(),
                companyName,
                c.getRole());
    }
}
