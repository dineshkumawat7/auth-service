package com.ebit.authentication.payloads;

import lombok.Data;

@Data
public class OAuth2UserDto {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String oauth2Id;
    private String oauth2Provider;
    private String imgUrl;
}
