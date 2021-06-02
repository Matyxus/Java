package chess2.main;
//https://www.chessprogramming.org/index.php?title=Looking_for_Magics
//rewritten code from C to java, code belongs to Tord Romstad, link is on line 2
import java.util.HashMap;
import java.util.Random;

public class FindingMagics {
    private static final Random generator = new Random();
    private static long[] a = new long[4096];
    private static long[] b = new long[4096];
    private static long[] used = new long[4096];
    private static HashMap<Integer, Long> bMagic;
    private static HashMap<Integer, Long> rMagic;
    
    private static final int RBits[] = {
        12, 11, 11, 11, 11, 11, 11, 12,
        11, 10, 10, 10, 10, 10, 10, 11,
        11, 10, 10, 10, 10, 10, 10, 11,
        11, 10, 10, 10, 10, 10, 10, 11,
        11, 10, 10, 10, 10, 10, 10, 11,
        11, 10, 10, 10, 10, 10, 10, 11,
        11, 10, 10, 10, 10, 10, 10, 11,
        12, 11, 11, 11, 11, 11, 11, 12
    };
    
    private static final int BBits[] = {
        6, 5, 5, 5, 5, 5, 5, 6,
        5, 5, 5, 5, 5, 5, 5, 5,
        5, 5, 7, 7, 7, 7, 5, 5,
        5, 5, 7, 9, 9, 7, 5, 5,
        5, 5, 7, 9, 9, 7, 5, 5,
        5, 5, 7, 7, 7, 7, 5, 5,
        5, 5, 5, 5, 5, 5, 5, 5,
        6, 5, 5, 5, 5, 5, 5, 6
    };
    
    private static final int BitTable[] = {
        63, 30, 3, 32, 25, 41, 22, 33, 15, 50, 42, 13, 11, 53, 19, 34, 61, 29, 2,
        51, 21, 43, 45, 10, 18, 47, 1, 54, 9, 57, 0, 35, 62, 31, 40, 4, 49, 5, 52,
        26, 60, 6, 23, 44, 46, 27, 56, 16, 7, 39, 48, 24, 59, 14, 12, 55, 38, 28,
        58, 20, 37, 17, 36, 8
    };

    private static long random_uint64() {
        long u1, u2, u3, u4;
        u1 = (long)(random()) & 0xFFFF; u2 = (long)(random()) & 0xFFFF;
        u3 = (long)(random()) & 0xFFFF; u4 = (long)(random()) & 0xFFFF;
        return u1 | (u2 << 16) | (u3 << 32) | (u4 << 48);
    }

    private static int random() {
        return generator.nextInt(32767);// rand() from C guarantees at least 32767 max number
    }

    private static long random_uint64_fewbits() {
        return random_uint64() & random_uint64() & random_uint64();
    }

    private static int count_1s(long b) {
        int r;
        for(r = 0;r < b; r++, b &= b - 1);
        return r;
    }

    private static int pop_1st_bit(long bb) {
        long b = bb ^ (bb - 1);
        int fold = (int)((b & 0xffffffff) ^ (b >>> 32));
        return BitTable[(fold * 0x783a9b23) >>> 26];
    }

    private static long index_to_uint64(int index, int bits, long m) {
        int i, j;
        long result = 0b0L;
        for(i = 0; i < bits; i++) {
            j = pop_1st_bit(m);
            m &= (m - 1);
            if((index & (1 << i))!=0) result |= (0b1L << j);
        }
        return result;
    }

    private static long rmask(int sq) {
        long result = 0b0L;
        int rk = sq/8, fl = sq%8, r, f;
        for(r = rk+1; r <= 6; r++) result |= (0b1L << (fl + r*8));
        for(r = rk-1; r >= 1; r--) result |= (0b1L << (fl + r*8));
        for(f = fl+1; f <= 6; f++) result |= (0b1L << (f + rk*8));
        for(f = fl-1; f >= 1; f--) result |= (0b1L << (f + rk*8));
        return result;
    }

    private static long bmask(int sq) {
        long result = 0b0L;
        int rk = sq/8, fl = sq%8, r, f;
        for(r=rk+1, f=fl+1; r<=6 && f<=6; r++, f++) result |= (0b1L << (f + r*8));
        for(r=rk+1, f=fl-1; r<=6 && f>=1; r++, f--) result |= (0b1L << (f + r*8));
        for(r=rk-1, f=fl+1; r>=1 && f<=6; r--, f++) result |= (0b1L << (f + r*8));
        for(r=rk-1, f=fl-1; r>=1 && f>=1; r--, f--) result |= (0b1L << (f + r*8));
        return result;
    }

    public static long ratt(int sq, long block) {
        long result = 0b0L;
        int rk = sq/8, fl = sq%8, r, f;
        for(r = rk+1; r <= 7; r++) {
            result |= (0b1L << (fl + r*8));
            if((block & (0b1L << (fl + r*8)))!=0) break;
        }
        for(r = rk-1; r >= 0; r--) {
            result |= (0b1L << (fl + r*8));
            if((block & (0b1L << (fl + r*8)))!=0) break;
        }
        for(f = fl+1; f <= 7; f++) {
            result |= (0b1L << (f + rk*8));
            if((block & (0b1L << (f + rk*8)))!=0) break;
        }
        for(f = fl-1; f >= 0; f--) {
            result |= (0b1L << (f + rk*8));
            if((block & (0b1L << (f + rk*8)))!=0) break;
        }
        return result;
    }

    public static long batt(int sq, long block) {
        long result = 0b0L;
        int rk = sq/8, fl = sq%8, r, f;
        for(r = rk+1, f = fl+1; r <= 7 && f <= 7; r++, f++) {
            result |= (0b1L << (f + r*8));
            if((block & (0b1L << (f + r * 8)))!=0) break;
        }
        for(r = rk+1, f = fl-1; r <= 7 && f >= 0; r++, f--) {
            result |= (0b1L << (f + r*8));
            if((block & (0b1L << (f + r * 8)))!=0) break;
        }
        for(r = rk-1, f = fl+1; r >= 0 && f <= 7; r--, f++) {
            result |= (0b1L << (f + r*8));
            if((block & (0b1L << (f + r * 8)))!=0) break;
        }
        for(r = rk-1, f = fl-1; r >= 0 && f >= 0; r--, f--) {
            result |= (0b1L << (f + r*8));
            if((block & (0b1L << (f + r * 8)))!=0) break;
        }
        return result;
    }

    private static int transform(long b, long magic, int bits) {
        return (int)((b * magic) >>> (64 - bits));
    }

    private static long find_magic(int sq, int m, boolean bishop) {
        long magic;
        long mask;
        int i, j, k, n, fail;

        mask = bishop ? bmask(sq) : rmask(sq);
        n = count_1s(mask);

        for(i = 0; i < (1 << n); i++) {
            b[i] = index_to_uint64(i, n, mask);
            a[i] = bishop ? batt(sq, b[i]) : ratt(sq, b[i]);
        }
        for(k = 0; k < 100000000; k++) {
            magic = random_uint64_fewbits();
            if(count_1s((mask * magic) & 0xFF00000000000000L) < 6) continue;
            for(i = 0; i < 4096; i++) used[i] = 0b0L;
            for(i = 0, fail = 0; (fail==0 && i < (1 << n)); i++) {
                j = transform(b[i], magic, m);
                if(used[j] == 0b0L){
                    used[j] = a[i];
                } else if(used[j] != a[i]){
                    fail = 1;
                }
            }
            if(fail == 1){
                return magic;
            } 
        }
        System.out.println("Failed");
        return 0;
    }
    
    private static void generateMagics(){
        int square;
        bMagic = new HashMap<Integer, Long>();
        rMagic = new HashMap<Integer, Long>();
        for(square = 0; square < 64; square++){
            bMagic.put(square, find_magic(square, BBits[square], true));
        }
        System.out.println(bMagic);
        used = new long[4096];
        a = new long[4096];
        b = new long[4096];
        for(square = 0; square < 64; square++){
            rMagic.put(square, find_magic(square, RBits[square], false));
        }
        System.out.println(rMagic);
    }

    public static void main(String[] args) {
        generateMagics();
    }

    public static HashMap<Integer, Long> getbMagic() {
        return bMagic;
    }
     
    public static HashMap<Integer, Long> getrMagic() {
        return rMagic;
    }
}