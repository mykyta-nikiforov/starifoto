import {defineStore} from 'pinia';
import type {AuthenticatedUser} from "@/dto/User";

interface State {
    user: AuthenticatedUser | null,
    accessToken: String | null,
    expiresIn: Number | null,
    isTokenRefreshing: boolean
}

export const useAuthStore = defineStore('auth',
    {
        state: (): State => {
            return {
                accessToken: null,
                expiresIn: null,
                user: null,
                isTokenRefreshing: false
            }
        },
        actions: {
            setAuthInfo(newAccessToken: string, newExpiresIn: number, newUser: AuthenticatedUser) {
                this.accessToken = newAccessToken;
                this.expiresIn = newExpiresIn;
                this.user = newUser;
            },
            clear() {
                this.accessToken = null;
                this.expiresIn = null;
                this.user = null;
            },
            setIsRefreshingToken(newValue: boolean) {
                this.isTokenRefreshing = newValue;
            }
        },
        getters: {
            isLoggedIn(): boolean {
                return this.user != null;
            },
            getUser(): AuthenticatedUser | null {
                return this.user;
            },
            isModeratorOrHigher(): boolean {
                return !!this.user
                    && !!this.user.roles
                    && this.user.roles.some(role => role.name === 'Moderator' || role.name === 'Admin');
            },
            isAdmin(): boolean {
                return !!this.user
                    && !!this.user.roles
                    && this.user.roles.some(role => role.name === 'Admin');
            },
            getIsTokenRefreshing(): boolean {
                return this.isTokenRefreshing;
            }
        }
    });
