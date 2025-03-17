export interface UpdateUserInfoRequest {
    username: string;
}

export interface UpdateUserPasswordRequest {
    oldPassword: string;
    newPassword: string;
}
