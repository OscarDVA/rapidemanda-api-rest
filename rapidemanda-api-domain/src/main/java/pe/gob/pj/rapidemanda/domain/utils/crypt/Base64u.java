package pe.gob.pj.rapidemanda.domain.utils.crypt;

public class Base64u extends Base64 {

	private static char[] vc;
    private static int[] cv;

    public Base64u() {
        this.spec1 = (char)45;
        this.spec2 = (char)95;
        this.spec3 = (char)42;
        this.initTables();
    }

    private void initTables() {
        if (vc == null) {
            int n;
            vc = new char[64];
            cv = new int[256];
            for (n = 0; n <= 25; ++n) {
                Base64u.vc[n] = (char)(65 + n);
            }
            for (n = 0; n <= 25; ++n) {
                Base64u.vc[n + 26] = (char)(97 + n);
            }
            for (n = 0; n <= 9; ++n) {
                Base64u.vc[n + 52] = (char)(48 + n);
            }
            Base64u.vc[62] = this.spec1;
            Base64u.vc[63] = this.spec2;
            for (n = 0; n < 256; ++n) {
                Base64u.cv[n] = -1;
            }
            for (n = 0; n < 64; ++n) {
                Base64u.cv[Base64u.vc[n]] = n;
            }
            Base64u.cv[this.spec3] = -2;
        }
        this.valueToChar = vc;
        this.charToValue = cv;
    }

    public static void main(String[] arrstring) {
    }
}
