package ua.in.photomap.user.service;

public interface EncryptionService {

    String encryptPassword(String password);

    boolean matchPassword(CharSequence rawPassword, String encodedPassword);
}
