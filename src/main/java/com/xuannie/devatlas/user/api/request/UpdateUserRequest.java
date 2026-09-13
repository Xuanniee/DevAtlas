package com.xuannie.devatlas.user.api.request;

import lombok.Getter;

/**
 * Update request that allows the User to update or multiple fields at once
 */
@Getter
public class UpdateUserRequest {
    private String newName;

    private String newEmail;

    private String newPassword;

    public UpdateUserRequest(String newName, String newEmail, String newPassword) {
        this.newName = newName;
        this.newEmail = newEmail;
        this.newPassword = newPassword;
    }
}
