package com.csec.CatholicTableMatching.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Service
public class EncryptService {

    private final AesBytesEncryptor encryptor;

    public String encrypt(String input) {
        byte[] encrypt = encryptor.encrypt(input.getBytes(StandardCharsets.UTF_8));
        return byteArrayToString(encrypt);
    }

    public String decrypt(String input) {
        byte[] decryptBytes = stringToByteArray(input);
        byte[] decrypt = encryptor.decrypt(decryptBytes);
        return new String(decrypt, StandardCharsets.UTF_8);
    }

    // byte -> String
    private String byteArrayToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte abyte : bytes) {
            sb.append(abyte);
            sb.append(" ");
        }
        return sb.toString();
    }

    // String -> byte
    private byte[] stringToByteArray(String byteString) {
        String[] split = byteString.split("\\s");
        ByteBuffer buffer = ByteBuffer.allocate(split.length);
        for (String s : split) {
            buffer.put((byte) Integer.parseInt(s));
        }
        return buffer.array();
    }

}
