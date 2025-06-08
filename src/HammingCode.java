public class HammingCode {

    public static int[] encodeData(int[] data) {
        int m = data.length;
        int r = 0;

        while (Math.pow(2, r) < (m + r + 1)) {
            r++;
        }

        int[] code = new int[m + r + 1];
        int j = 0;

        for (int i = 1; i < code.length; i++) {
            if (Math.pow(2, j) == i) {
                j++;
            } else {
                code[i] = data[i - j - 1];
            }
        }

        for (int i = 0; i < r; i++) {
            int pos = (int) Math.pow(2, i);
            int parity = 0;
            for (int k = 1; k < code.length; k++) {
                if (((k >> i) & 1) == 1) {
                    parity ^= code[k];
                }
            }
            code[pos] = parity;
        }

        int overall = 0;
        for (int i = 1; i < code.length; i++) {
            overall ^= code[i];
        }
        code[0] = overall;

        return code;
    }

    public static int[] introduceError(int[] code, int pos) {
        code[pos] ^= 1;
        return code;
    }

    public static int detectAndCorrect(int[] code) {
        int r = 0;
        while (Math.pow(2, r) < code.length) {
            r++;
        }

        int syndrome = 0;
        for (int i = 0; i < r; i++) {
            int pos = (int) Math.pow(2, i);
            int parity = 0;
            for (int k = 1; k < code.length; k++) {
                if (((k >> i) & 1) == 1) {
                    parity ^= code[k];
                }
            }
            if (code[pos] != parity) {
                syndrome += pos;
            }
        }

        int overall = 0;
        for (int i = 1; i < code.length; i++) {
            overall ^= code[i];
        }
        overall ^= code[0];

        if (syndrome == 0 && overall == 0) {
            System.out.println("Hata yok.");
            return -2;
        } else if (syndrome != 0 && overall == 1) {
            code[syndrome] ^= 1;
            System.out.println("Tek bitlik hata bulundu ve düzeltildi. Pozisyon: " + syndrome);
            return syndrome;
        } else if (syndrome == 0 && overall == 1) {
            System.out.println("Çift bitlik hata tespit edildi. DÜZELTİLEMEZ.");
            return -1;
        } else {
            System.out.println("Bilinmeyen hata durumu.");
            return -1;
        }
    }
}