package com.deliverytech.delivery_api.dto;

import com.deliverytech.delivery_api.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String nome;
    private String email;
    private Role role;
    private Long restauranteId;
}