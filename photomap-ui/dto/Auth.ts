import type {AuthenticatedUser} from "~/dto/User";

export interface GoogleAuthRequest {
    credential: string;
}

export interface SignUpRequest {
    username: string;
    email: string;
    password: string;
}

export interface LoginRequest {
    email: string;
    password: string;
}

export interface AuthResponse {
    user: AuthenticatedUser;
    accessToken: string;
    expiresIn: number;
    refreshToken: string;
    refreshTokenExpiresIn: string;
}