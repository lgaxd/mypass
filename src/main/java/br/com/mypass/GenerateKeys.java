package br.com.mypass;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class GenerateKeys {

    public static void main(String[] args) throws Exception {

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);

        KeyPair keyPair = generator.generateKeyPair();

        Path keysDirectory = Path.of("src/main/resources/keys");

        Files.createDirectories(keysDirectory);

        String privateKey = """
                -----BEGIN PRIVATE KEY-----
                %s
                -----END PRIVATE KEY-----
                """.formatted(
                Base64.getMimeEncoder(64, new byte[]{'\n'})
                        .encodeToString(keyPair.getPrivate().getEncoded())
        );

        String publicKey = """
                -----BEGIN PUBLIC KEY-----
                %s
                -----END PUBLIC KEY-----
                """.formatted(
                Base64.getMimeEncoder(64, new byte[]{'\n'})
                        .encodeToString(keyPair.getPublic().getEncoded())
        );

        Files.writeString(
                keysDirectory.resolve("private_key.pem"),
                privateKey
        );

        Files.writeString(
                keysDirectory.resolve("public_key.pem"),
                publicKey
        );

        System.out.println("=================================");
        System.out.println("Chaves RSA geradas com sucesso!");
        System.out.println("=================================");
    }
}