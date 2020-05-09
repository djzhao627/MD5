package cn.djzhao;

import java.security.MessageDigest;

public class JavaMd5 {
    public static void main(String[] args) throws Exception {
        System.out.println(MD5("djzhao", 16));
    }

    public static String MD5(String text, int length) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(text.getBytes());
        byte[] bytes = digest.digest();

        StringBuilder result = new StringBuilder();
        for (byte aByte : bytes) {
            int temp;
            if (aByte < 0) {
                temp = 256 + aByte;
            } else {
                temp = aByte;
            }
            if (temp < 16) {
                result.append("0");
            }
            result.append(Integer.toHexString(temp));
        }
        return result.toString();
    }
}
