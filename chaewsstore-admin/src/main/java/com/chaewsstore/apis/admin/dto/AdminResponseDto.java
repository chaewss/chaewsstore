package com.chaewsstore.apis.admin.dto;

import com.chaewsstore.core.domain.admin.Admin;

public record AdminResponseDto(
    Long id,
    String username,
    String name
) {

    public static AdminResponseDto from(Admin admin) {
        return new AdminResponseDto(admin.getId(), admin.getUsername(), admin.getName());
    }
}
