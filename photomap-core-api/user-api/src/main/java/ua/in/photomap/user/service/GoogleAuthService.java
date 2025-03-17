package ua.in.photomap.user.service;

import ua.in.photomap.user.model.User;

public interface GoogleAuthService {

    User getUserInfo(String googleAuthToken);
}