package cn.turing.common.util;

/**
 * java 如何将十六进制字符串转换为 float 符点型？相互转换
 * Hex2Float
 *
 * @author 微wx笑
 * @date 2017年12月6日下午5:22:10
 */
public class Hex2Float {
    public static void main(String[] args) {

//02 00 01 09 54 55 52 00 00 01 00 02 f5 a5 5f 2a 00 22 00 25 00 2d 00 22 00 25 00 2d 04 88 00 c0 00 14 00 08 00 04 00 04 00 00 80 3f cd 27 c1 41 ca 57 38 42 55 5a
        String hexString = "42396464";//BE 04 C8 41 //00 00 80 3F //64 64 39 42
        Long l = Hex2Float.parseLong(hexString, 16);
        Float f = Float.intBitsToFloat(l.intValue());


    }

    /**
     * 代码来自：java.lang.Long
     * 因为要跟踪看变量的值，所以要copy出来，或者是去附加源码，否则 eclipse 调试时查看变量的值会提示 xxx cannot be resolved to a variable
     *
     * @param s
     * @param radix
     * @return
     * @throws NumberFormatException
     * @author 微wx笑
     * @date 2017年12月6日下午5:19:40
     */
    public static long parseLong(String s, int radix) throws NumberFormatException {
        if (s == null) {
            throw new NumberFormatException("null");
        }

        if (radix < Character.MIN_RADIX) {
            throw new NumberFormatException("radix " + radix + " less than Character.MIN_RADIX");
        }
        if (radix > Character.MAX_RADIX) {
            throw new NumberFormatException("radix " + radix + " greater than Character.MAX_RADIX");
        }

        long result = 0;
        boolean negative = false;
        int i = 0, len = s.length();
        long limit = -Long.MAX_VALUE;
        long multmin;
        int digit;

        if (len > 0) {
            char firstChar = s.charAt(0);
            if (firstChar < '0') { // Possible leading "+" or "-"
                if (firstChar == '-') {
                    negative = true;
                    limit = Long.MIN_VALUE;
                } else if (firstChar != '+')
                    throw NumberFormatException.forInputString(s);

                if (len == 1) // Cannot have lone "+" or "-"
                    throw NumberFormatException.forInputString(s);
                i++;
            }
            multmin = limit / radix;
            while (i < len) {
                // Accumulating negatively avoids surprises near MAX_VALUE
                digit = Character.digit(s.charAt(i++), radix);
                if (digit < 0) {
                    throw NumberFormatException.forInputString(s);
                }
                if (result < multmin) {
                    throw NumberFormatException.forInputString(s);
                }
                result *= radix;
                if (result < limit + digit) {
                    throw NumberFormatException.forInputString(s);
                }
                result -= digit;
            }
        } else {
            throw NumberFormatException.forInputString(s);
        }
        return negative ? result : -result;
    }
}

/**
 * 代码来自：java.lang.NumberFormatException
 * NumberFormatException
 *
 * @author 微wx笑
 * @date 2017年12月6日下午5:20:36
 */
class NumberFormatException extends IllegalArgumentException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public NumberFormatException(String s) {
        super(s);
    }

    static NumberFormatException forInputString(String s) {
        return new NumberFormatException("For input string: \"" + s + "\"");
    }



}
