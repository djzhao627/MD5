package cn.djzhao;
/*
 * MD5原理
 * 1. 填充字节
 * 在原消息中添加填充位，添加的长度在1到512之间。
 * 从而使得填充后的消息长度等于一个值，比512位的倍数少64位。
 * 例如原消息100位，填充348位，得到448位，448=512-64。
 * 其中，这没有填充的64位用来记录原消息的长度值，如果超过了2的64方位，就取低64位。
 * 2. 分块
 * 将整个消息长度分为512位的倍数块，每块由16个32比特的字构成。
 * 3. 初始化寄存器
 * MD5缓冲区初始化算法要使用128位长的缓冲区以存储中间结果和最终Hash值。
 * 缓冲区用4个32比特长的寄存器A，B，C，D构成。
 * 每个寄存器的初始十六进制值分别为A=HEX(01234567)，B=HEX(89ABCDEF)，C=HEX(FEDCBA98)，D=HEX(76543210)，是标准的幻数。
 * 4. 处理每一个分块
 * 每个分块都由压缩函数H()处理，压缩函数是算法的核心。包括四轮处理，每轮16步，总共64步。
 * 5. 输出结果
 * 每个分块处理后，最后压缩函数的输出即为产生的消息摘要。
 * */

/**
 * MD5实现
 *
 * @author djzhao
 * @time 2020/05/09 23:03
 */
public class MD5 {

    //标准的幻数
    private static final int A = 0X67452301;
    private static final int B = 0XEFCDAB89;
    private static final int C = 0X98BADCFE;
    private static final int D = 0X10325476;

    // 在四轮循环运算中用到
    private static final int[] SHIFT =
            {7, 12, 17, 22, 5, 9, 14, 20, 4, 11, 16, 23, 6, 10, 15, 21};

    //存储hash结果，共4×32=128位，初始化值为（幻数的级联）
    private final long[] result = {A, B, C, D};

    // 存储四轮运算时所使用的正弦函数值（4组，每组16个）
    private static final int[] TABLE_T = new int[64];

    // 计算每一个正弦函数值(初始化)
    static {
        for (int i = 0; i < 64; i++)
            TABLE_T[i] = (int) (long) ((1L << 32) * Math.abs(Math.sin(i + 1)));
    }

    /**
     * 计算MD5
     *
     * @param message 输入的信息
     * @return 32位MD5值
     */
    public static String messageDigest5(String message) {
        byte[] messageBytes = message.getBytes();
        // 消息长度
        int messageLen = messageBytes.length;
        // 组数
        int numBlocks = ((messageLen + 8) / 64) + 1;
        // 总长度
        int totalLen = numBlocks * 64;
        // 最后的填充位
        byte[] paddingBytes = new byte[totalLen - messageLen];
        // 填充数组
        paddingBytes[0] = (byte) 0x80;
        // 消息总位数
        long messageLenBits = (long) messageLen * 8;
        // 存储原来消息的长度值（后64位）
        for (int i = 0; i < 8; i++) {
            paddingBytes[paddingBytes.length - 8 + i] = (byte) messageLenBits;
            messageLenBits /= 256;
        }
        int a = A;
        int b = B;
        int c = C;
        int d = D;
        // 存储结果（消息分组）
        int[] buffer = new int[16];
        // 分组计算
        for (int i = 0; i < numBlocks; i++) {
            int index = i * 64;
            for (int j = 0; j < 64; j++, index++)
                buffer[j / 4] = ((int) ((index < messageLen) ? messageBytes[index]
                        : paddingBytes[index - messageLen]) << 24)
                        | (buffer[j >>> 2] >>> 8);
            int originalA = a;
            int originalB = b;
            int originalC = c;
            int originalD = d;
            // 小分组计算（分块）,四轮运算
            for (int j = 0; j < 64; j++) {
                int div16 = j / 16;
                int f = 0;
                int bufferIndex = j;
                // 四个指定的线性函数运算（重点，看文档说明）
                switch (div16) {
                    case 0:
                        f = (b & c) | (~b & d);
                        break;
                    case 1:
                        f = (b & d) | (c & ~d);
                        bufferIndex = (bufferIndex * 5 + 1) & 0x0F;
                        break;
                    case 2:
                        f = b ^ c ^ d;
                        bufferIndex = (bufferIndex * 3 + 5) & 0x0F;
                        break;
                    case 3:
                        f = c ^ (b | ~d);
                        bufferIndex = (bufferIndex * 7) & 0x0F;
                        break;
                }
                // 指定操作（重点，看文档说明）
                int temp = b
                        + Integer.rotateLeft(a + f + buffer[bufferIndex]
                                + TABLE_T[j],
                        SHIFT[(div16 << 2) | (j & 3)]);
                a = d;
                d = c;
                c = b;
                b = temp;
            }
            a += originalA;
            b += originalB;
            c += originalC;
            d += originalD;
        }
        byte[] md5 = new byte[16];
        int count = 0;
        // 转为长度32的比特数组
        for (int i = 0; i < 4; i++) {
            int n = (i == 0) ? a : ((i == 1) ? b : ((i == 2) ? c : d));
            for (int j = 0; j < 4; j++) {
                md5[count++] = (byte) n;
                n >>>= 8;
            }
        }
        return toHexString(md5);
    }

    /**
     * 转为十六进制的字符串
     *
     * @param b 比特数组
     * @return 对应的16进制
     */
    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            sb.append(String.format("%02X", b[i] & 0xFF));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String[] testStrings = {"", "应用密码学", "Message Digest 5", "119106032875 赵冬晋"};
        for (String s : testStrings) {
            System.out.println(String.format("%s is MD5 of \"%s\"", messageDigest5(s), s));
        }
    }
}
