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
 * 每个寄存器的初始十六进制值分别为A=HEX(01234567)，B=HEX(89ABCDEF)，C=HEX(FEDCBA98)，D=HEX(76543210)
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

    /**
     * 十六进制
     */
    static final String HEXS[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};

    //标准的幻数
    private static final long A=0X67452301L;
    private static final long B=0XEFCDAB89L;
    private static final long C=0X98BADCFEL;
    private static final long D=0X10325476L;

    public static void main(String[] args) {
        System.out.println(402323341);
        System.out.println(Integer.MAX_VALUE);
    }
}
