export interface AuthenticatedUser {
    id: number;
    email: string;
    username: string;
    hasPassword: boolean;
    provider: String
    roles: RoleDTO[];
}

export interface UserDTO {
    id: number,
    username: String,
    email: String,
    provider: String
    roles: RoleDTO[],
    createdAt: Date,
    isEnabled: boolean
}

export interface RoleDTO {
    id: number,
    name: string
}

export interface UpdateUserPayload {
    username: string;
    email: string;
    rolesIds: number[];
}

export interface UserProfileDTO {
    id: number;
    username: string;
    createdAt: string;
}
