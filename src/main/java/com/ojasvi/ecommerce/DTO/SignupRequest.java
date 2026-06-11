package com.ojasvi.ecommerce.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {

    private String fullName;
    private String email;
    private String mobile;
    private String password;

}
