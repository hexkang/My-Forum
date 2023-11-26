package com.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class ModifyEmailVo {
    @Email
    String email;
    @Length(max = 6,min = 6)
    String code;
}
