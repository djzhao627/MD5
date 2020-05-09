package cn.djzhao;

public class MD52 {

    static final string hexs[]={"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};
    //标准的幻数
    private static final long a=0x67452301l;
    private static final long b=0xefcdab89l;
    private static final long c=0x98badcfel;
    private static final long d=0x10325476l;


    //下面这些s11-s44实际上是一个4*4的矩阵，在四轮循环运算中用到
    static final int s11 = 7;
    static final int s12 = 12;
    static final int s13 = 17;
    static final int s14 = 22;

    static final int s21 = 5;
    static final int s22 = 9;
    static final int s23 = 14;
    static final int s24 = 20;

    static final int s31 = 4;
    static final int s32 = 11;
    static final int s33 = 16;
    static final int s34 = 23;

    static final int s41 = 6;
    static final int s42 = 10;
    static final int s43 = 15;
    static final int s44 = 21;

    //java不支持无符号的基本数据（unsigned）
    private long [] result={a,b,c,d};//存储hash结果，共4×32=128位，初始化值为（幻数的级联）

    public static void main(string []args){
        md52 md=new md52();
        system.out.println("md5(abc)="+md.digest("djzhao"));
    }

    private string digest(string inputstr){
        byte [] inputbytes=inputstr.getbytes();
        int bytelen=inputbytes.length;//长度（字节）
        int groupcount=0;//完整分组的个数
        groupcount=bytelen/64;//每组512位（64字节）
        long []groups=null;//每个小组(64字节)再细分后的16个小组(4字节)

        //处理每一个完整 分组
        for(int step=0;step<groupcount;step++){
            groups=divgroup(inputbytes,step*64);
            trans(groups);//处理分组，核心算法
        }

        //处理完整分组后的尾巴
        int rest=bytelen%64;//512位分组后的余数
        byte [] tempbytes=new byte[64];
        if(rest<56){
            for(int i=0;i<rest;i++)
                tempbytes[i]=inputbytes[bytelen-rest+i];
            if(rest<56){
                tempbytes[rest]=(byte)(1<<7);
                for(int i=1;i<56-rest;i++)
                    tempbytes[rest+i]=0;
            }
            long len=(long)(bytelen<<3);
            for(int i=0;i<8;i++){
                tempbytes[56+i]=(byte)(len&0xffl);
                len=len>>8;
            }
            groups=divgroup(tempbytes,0);
            trans(groups);//处理分组
        }else{
            for(int i=0;i<rest;i++)
                tempbytes[i]=inputbytes[bytelen-rest+i];
            tempbytes[rest]=(byte)(1<<7);
            for(int i=rest+1;i<64;i++)
                tempbytes[i]=0;
            groups=divgroup(tempbytes,0);
            trans(groups);//处理分组

            for(int i=0;i<56;i++)
                tempbytes[i]=0;
            long len=(long)(bytelen<<3);
            for(int i=0;i<8;i++){
                tempbytes[56+i]=(byte)(len&0xffl);
                len=len>>8;
            }
            groups=divgroup(tempbytes,0);
            trans(groups);//处理分组
        }

        //将hash值转换成十六进制的字符串
        string resstr="";
        long temp=0;
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                temp=result[i]&0x0fl;
                string a=hexs[(int)(temp)];
                result[i]=result[i]>>4;
                temp=result[i]&0x0fl;
                resstr+=hexs[(int)(temp)]+a;
                result[i]=result[i]>>4;
            }
        }
        return resstr;
    }

    /**
     * 从inputbytes的index开始取512位，作为新的分组
     * 将每一个512位的分组再细分成16个小组，每个小组64位（8个字节）
     * @param inputbytes
     * @param index
     * @return
     */
    private static long[] divgroup(byte[] inputbytes,int index){
        long [] temp=new long[16];
        for(int i=0;i<16;i++){
            temp[i]=b2iu(inputbytes[4*i+index])|
                    (b2iu(inputbytes[4*i+1+index]))<<8|
                    (b2iu(inputbytes[4*i+2+index]))<<16|
                    (b2iu(inputbytes[4*i+3+index]))<<24;
        }
        return temp;
    }

    /**
     * 这时不存在符号位（符号位存储不再是代表正负），所以需要处理一下
     * @param b
     * @return
     */
    public static long b2iu(byte b){
        return b < 0 ? b & 0x7f + 128 : b;
    }

    /**
     * 主要的操作，四轮循环
     * @param groups--每一个分组512位（64字节）
     */
    private void trans(long[] groups) {
        long a = result[0], b = result[1], c = result[2], d = result[3];
        /*第一轮*/
        a = ff(a, b, c, d, groups[0], s11, 0xd76aa478l); /* 1 */
        d = ff(d, a, b, c, groups[1], s12, 0xe8c7b756l); /* 2 */
        c = ff(c, d, a, b, groups[2], s13, 0x242070dbl); /* 3 */
        b = ff(b, c, d, a, groups[3], s14, 0xc1bdceeel); /* 4 */
        a = ff(a, b, c, d, groups[4], s11, 0xf57c0fafl); /* 5 */
        d = ff(d, a, b, c, groups[5], s12, 0x4787c62al); /* 6 */
        c = ff(c, d, a, b, groups[6], s13, 0xa8304613l); /* 7 */
        b = ff(b, c, d, a, groups[7], s14, 0xfd469501l); /* 8 */
        a = ff(a, b, c, d, groups[8], s11, 0x698098d8l); /* 9 */
        d = ff(d, a, b, c, groups[9], s12, 0x8b44f7afl); /* 10 */
        c = ff(c, d, a, b, groups[10], s13, 0xffff5bb1l); /* 11 */
        b = ff(b, c, d, a, groups[11], s14, 0x895cd7bel); /* 12 */
        a = ff(a, b, c, d, groups[12], s11, 0x6b901122l); /* 13 */
        d = ff(d, a, b, c, groups[13], s12, 0xfd987193l); /* 14 */
        c = ff(c, d, a, b, groups[14], s13, 0xa679438el); /* 15 */
        b = ff(b, c, d, a, groups[15], s14, 0x49b40821l); /* 16 */

        /*第二轮*/
        a = gg(a, b, c, d, groups[1], s21, 0xf61e2562l); /* 17 */
        d = gg(d, a, b, c, groups[6], s22, 0xc040b340l); /* 18 */
        c = gg(c, d, a, b, groups[11], s23, 0x265e5a51l); /* 19 */
        b = gg(b, c, d, a, groups[0], s24, 0xe9b6c7aal); /* 20 */
        a = gg(a, b, c, d, groups[5], s21, 0xd62f105dl); /* 21 */
        d = gg(d, a, b, c, groups[10], s22, 0x2441453l); /* 22 */
        c = gg(c, d, a, b, groups[15], s23, 0xd8a1e681l); /* 23 */
        b = gg(b, c, d, a, groups[4], s24, 0xe7d3fbc8l); /* 24 */
        a = gg(a, b, c, d, groups[9], s21, 0x21e1cde6l); /* 25 */
        d = gg(d, a, b, c, groups[14], s22, 0xc33707d6l); /* 26 */
        c = gg(c, d, a, b, groups[3], s23, 0xf4d50d87l); /* 27 */
        b = gg(b, c, d, a, groups[8], s24, 0x455a14edl); /* 28 */
        a = gg(a, b, c, d, groups[13], s21, 0xa9e3e905l); /* 29 */
        d = gg(d, a, b, c, groups[2], s22, 0xfcefa3f8l); /* 30 */
        c = gg(c, d, a, b, groups[7], s23, 0x676f02d9l); /* 31 */
        b = gg(b, c, d, a, groups[12], s24, 0x8d2a4c8al); /* 32 */

        /*第三轮*/
        a = hh(a, b, c, d, groups[5], s31, 0xfffa3942l); /* 33 */
        d = hh(d, a, b, c, groups[8], s32, 0x8771f681l); /* 34 */
        c = hh(c, d, a, b, groups[11], s33, 0x6d9d6122l); /* 35 */
        b = hh(b, c, d, a, groups[14], s34, 0xfde5380cl); /* 36 */
        a = hh(a, b, c, d, groups[1], s31, 0xa4beea44l); /* 37 */
        d = hh(d, a, b, c, groups[4], s32, 0x4bdecfa9l); /* 38 */
        c = hh(c, d, a, b, groups[7], s33, 0xf6bb4b60l); /* 39 */
        b = hh(b, c, d, a, groups[10], s34, 0xbebfbc70l); /* 40 */
        a = hh(a, b, c, d, groups[13], s31, 0x289b7ec6l); /* 41 */
        d = hh(d, a, b, c, groups[0], s32, 0xeaa127fal); /* 42 */
        c = hh(c, d, a, b, groups[3], s33, 0xd4ef3085l); /* 43 */
        b = hh(b, c, d, a, groups[6], s34, 0x4881d05l); /* 44 */
        a = hh(a, b, c, d, groups[9], s31, 0xd9d4d039l); /* 45 */
        d = hh(d, a, b, c, groups[12], s32, 0xe6db99e5l); /* 46 */
        c = hh(c, d, a, b, groups[15], s33, 0x1fa27cf8l); /* 47 */
        b = hh(b, c, d, a, groups[2], s34, 0xc4ac5665l); /* 48 */

        /*第四轮*/
        a = ii(a, b, c, d, groups[0], s41, 0xf4292244l); /* 49 */
        d = ii(d, a, b, c, groups[7], s42, 0x432aff97l); /* 50 */
        c = ii(c, d, a, b, groups[14], s43, 0xab9423a7l); /* 51 */
        b = ii(b, c, d, a, groups[5], s44, 0xfc93a039l); /* 52 */
        a = ii(a, b, c, d, groups[12], s41, 0x655b59c3l); /* 53 */
        d = ii(d, a, b, c, groups[3], s42, 0x8f0ccc92l); /* 54 */
        c = ii(c, d, a, b, groups[10], s43, 0xffeff47dl); /* 55 */
        b = ii(b, c, d, a, groups[1], s44, 0x85845dd1l); /* 56 */
        a = ii(a, b, c, d, groups[8], s41, 0x6fa87e4fl); /* 57 */
        d = ii(d, a, b, c, groups[15], s42, 0xfe2ce6e0l); /* 58 */
        c = ii(c, d, a, b, groups[6], s43, 0xa3014314l); /* 59 */
        b = ii(b, c, d, a, groups[13], s44, 0x4e0811a1l); /* 60 */
        a = ii(a, b, c, d, groups[4], s41, 0xf7537e82l); /* 61 */
        d = ii(d, a, b, c, groups[11], s42, 0xbd3af235l); /* 62 */
        c = ii(c, d, a, b, groups[2], s43, 0x2ad7d2bbl); /* 63 */
        b = ii(b, c, d, a, groups[9], s44, 0xeb86d391l); /* 64 */

        /*加入到之前计算的结果当中*/
        result[0] += a;
        result[1] += b;
        result[2] += c;
        result[3] += d;
        result[0]=result[0]&0xffffffffl;
        result[1]=result[1]&0xffffffffl;
        result[2]=result[2]&0xffffffffl;
        result[3]=result[3]&0xffffffffl;
    }

    /**
     * 下面是处理要用到的线性函数
     */
    private static long f(long x, long y, long z) {
        return (x & y) | ((~x) & z);
    }

    private static long g(long x, long y, long z) {
        return (x & z) | (y & (~z));
    }

    private static long h(long x, long y, long z) {
        return x ^ y ^ z;
    }

    private static long i(long x, long y, long z) {
        return y ^ (x | (~z));
    }

    private static long ff(long a, long b, long c, long d, long x, long s,
                           long ac) {
        a += (f(b, c, d)&0xffffffffl) + x + ac;
        a = ((a&0xffffffffl)<< s) | ((a&0xffffffffl) >>> (32 - s));
        a += b;
        return (a&0xffffffffl);
    }

    private static long gg(long a, long b, long c, long d, long x, long s,
                           long ac) {
        a += (g(b, c, d)&0xffffffffl) + x + ac;
        a = ((a&0xffffffffl) << s) | ((a&0xffffffffl) >>> (32 - s));
        a += b;
        return (a&0xffffffffl);
    }

    private static long hh(long a, long b, long c, long d, long x, long s,
                           long ac) {
        a += (h(b, c, d)&0xffffffffl) + x + ac;
        a = ((a&0xffffffffl) << s) | ((a&0xffffffffl) >>> (32 - s));
        a += b;
        return (a&0xffffffffl);
    }

    private static long ii(long a, long b, long c, long d, long x, long s,
                           long ac) {
        a += (i(b, c, d)&0xffffffffl) + x + ac;
        a = ((a&0xffffffffl) << s) | ((a&0xffffffffl) >>> (32 - s));
        a += b;
        return (a&0xffffffffl);
    }
}
