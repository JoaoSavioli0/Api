package com.condolives.api.dto.staff;

import java.time.LocalDate;
import java.util.UUID;

import com.condolives.api.entity.User.Company;
import com.condolives.api.entity.User.Staff;
import com.condolives.api.entity.User.UserAccount;
import com.condolives.api.enums.StaffCategory;

public record StaffResponse(
        UUID id,
        UUID condominiumId,
        String name,
        String phone,
        String cpf,
        String rg,
        String jobTitle,
        StaffCategory category,
        String address,
        LocalDate joinedAt,
        Boolean active,
        String inviteCode,
        UserInfo user,
        CompanyInfo company) {

    public record UserInfo(UUID id, String name, String email, String phone, String cpf, String rg, String avatarUrl) {
    }

    public record CompanyInfo(UUID id, String name, String cnpj, String phone, String email) {
    }

    public static StaffResponse from(Staff s) {
        return from(s, null);
    }

    public static StaffResponse from(Staff s, String inviteCode) {
        UserAccount u = s.getUser();
        Company c = s.getCompany();

        String name  = u != null ? u.getName()  : s.getName();
        String phone = u != null ? u.getPhone()  : s.getPhone();
        String cpf   = u != null ? u.getCpf()    : s.getCpf();
        String rg    = u != null ? u.getRg()     : s.getRg();

        return new StaffResponse(
                s.getId(),
                s.getCondominiumId(),
                name,
                phone,
                cpf,
                rg,
                s.getJobTitle(),
                s.getCategory(),
                s.getAddress(),
                s.getJoinedAt(),
                s.getActive(),
                inviteCode,
                u != null ? new UserInfo(u.getId(), u.getName(), u.getEmail(), u.getPhone(),
                        u.getCpf(), u.getRg(), u.getAvatarUrl()) : null,
                c != null ? new CompanyInfo(c.getId(), c.getName(), c.getCnpj(), c.getPhone(), c.getEmail()) : null);
    }
}
