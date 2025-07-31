package pe.gob.pj.rapidemanda.domain.utils.crypt;

public class Base64 {
	    private String lineSeparator = System.getProperty("line.separator");
	    private int lineLength = 72;
	    char[] valueToChar;
	    int[] charToValue;
	    char spec1 = (char)43;
	    char spec2 = (char)47;
	    char spec3 = (char)61;
	    private static char[] vc;
	    private static int[] cv;
	    static final int IGNORE = -1;
	    static final int PAD = -2;
	    public static final boolean DEBUGGING = false;

	    public Base64() {
	        this.spec1 = (char)43;
	        this.spec2 = (char)47;
	        this.spec3 = (char)61;
	        this.initTables();
	    }

	    public String encode(byte[] arrby) {
	        int n;
	        int n2 = (arrby.length + 2) / 3 * 4;
	        if (this.lineLength != 0 && (n = (n2 + this.lineLength - 1) / this.lineLength - 1) > 0) {
	            n2 += n * this.lineSeparator.length();
	        }
	        StringBuffer stringBuffer = new StringBuffer(n2);
	        int n3 = 0;
	        int n4 = arrby.length / 3 * 3;
	        int n5 = arrby.length - n4;
	        for (int i = 0; i < n4; i += 3) {
	            if ((n3 += 4) > this.lineLength) {
	                if (this.lineLength != 0) {
	                    stringBuffer.append(this.lineSeparator);
	                }
	                n3 = 4;
	            }
	            int n6 = arrby[i + 0] & 0xFF;
	            n6 <<= 8;
	            n6 |= arrby[i + 1] & 0xFF;
	            n6 <<= 8;
	            int n7 = (n6 |= arrby[i + 2] & 0xFF) & 0x3F;
	            int n8 = (n6 >>>= 6) & 0x3F;
	            int n9 = (n6 >>>= 6) & 0x3F;
	            int n10 = (n6 >>>= 6) & 0x3F;
	            stringBuffer.append(this.valueToChar[n10]);
	            stringBuffer.append(this.valueToChar[n9]);
	            stringBuffer.append(this.valueToChar[n8]);
	            stringBuffer.append(this.valueToChar[n7]);
	        }
	        switch (n5) {
	            default: {
	                break;
	            }
	            case 1: {
	                if ((n3 += 4) > this.lineLength) {
	                    if (this.lineLength != 0) {
	                        stringBuffer.append(this.lineSeparator);
	                    }
	                    n3 = 4;
	                }
	                stringBuffer.append(this.encode(new byte[]{arrby[n4], 0, 0}).substring(0, 2));
	                stringBuffer.append(this.spec3);
	                stringBuffer.append(this.spec3);
	                break;
	            }
	            case 2: {
	                if ((n3 += 4) > this.lineLength) {
	                    if (this.lineLength != 0) {
	                        stringBuffer.append(this.lineSeparator);
	                    }
	                    n3 = 4;
	                }
	                stringBuffer.append(this.encode(new byte[]{arrby[n4], arrby[n4 + 1], 0}).substring(0, 3));
	                stringBuffer.append(this.spec3);
	            }
	        }
	        if (n2 != stringBuffer.length()) {
	            System.out.println("oops: minor program flaw: output length mis-estimated");
	            System.out.println("estimate:" + n2);
	            System.out.println("actual:" + stringBuffer.length());
	        }
	        return stringBuffer.toString();
	    }

	    public byte[] decode(String string) {
	        byte[] arrby = new byte[string.length() / 4 * 3];
	        int n = 0;
	        int n2 = 0;
	        int n3 = 0;
	        int n4 = string.length();
	        int n5 = 0;
	        block10: for (int i = 0; i < n4; ++i) {
	            char c = string.charAt(i);
	            int n6 = c <= '\u00ff' ? this.charToValue[c] : -1;
	            switch (n6) {
	                case -1: {
	                    continue block10;
	                }
	                case -2: {
	                    n6 = 0;
	                    ++n5;
	                }
	                default: {
	                    switch (n) {
	                        case 0: {
	                            n2 = n6;
	                            n = 1;
	                            continue block10;
	                        }
	                        case 1: {
	                            n2 <<= 6;
	                            n2 |= n6;
	                            n = 2;
	                            continue block10;
	                        }
	                        case 2: {
	                            n2 <<= 6;
	                            n2 |= n6;
	                            n = 3;
	                            continue block10;
	                        }
	                        case 3: {
	                            n2 <<= 6;
	                            arrby[n3 + 2] = (byte)(n2 |= n6);
	                            arrby[n3 + 1] = (byte)(n2 >>>= 8);
	                            arrby[n3] = (byte)(n2 >>>= 8);
	                            n3 += 3;
	                            n = 0;
	                        }
	                    }
	                }
	            }
	        }
	        if (n != 0) {
	            throw new ArrayIndexOutOfBoundsException("Input to decode not an even multiple of 4 characters; pad with " + this.spec3);
	        }
	        if (arrby.length != (n3 -= n5)) {
	            byte[] arrby2 = new byte[n3];
	            System.arraycopy(arrby, 0, arrby2, 0, n3);
	            arrby = arrby2;
	        }
	        return arrby;
	    }

	    public void setLineLength(int n) {
	        this.lineLength = n / 4 * 4;
	    }

	    public void setLineSeparator(String string) {
	        this.lineSeparator = string;
	    }

	    private void initTables() {
	        if (vc == null) {
	            int n;
	            vc = new char[64];
	            cv = new int[256];
	            for (n = 0; n <= 25; ++n) {
	                Base64.vc[n] = (char)(65 + n);
	            }
	            for (n = 0; n <= 25; ++n) {
	                Base64.vc[n + 26] = (char)(97 + n);
	            }
	            for (n = 0; n <= 9; ++n) {
	                Base64.vc[n + 52] = (char)(48 + n);
	            }
	            Base64.vc[62] = this.spec1;
	            Base64.vc[63] = this.spec2;
	            for (n = 0; n < 256; ++n) {
	                Base64.cv[n] = -1;
	            }
	            for (n = 0; n < 64; ++n) {
	                Base64.cv[Base64.vc[n]] = n;
	            }
	            Base64.cv[this.spec3] = -2;
	        }
	        this.valueToChar = vc;
	        this.charToValue = cv;
	    }

	    public static void show(byte[] arrby) {
	        for (int i = 0; i < arrby.length; ++i) {
	            System.out.print(Integer.toHexString(arrby[i] & 0xFF) + " ");
	        }
	        System.out.println();
	    }

	    public static void display(byte[] arrby) {
	        for (int i = 0; i < arrby.length; ++i) {
	            System.out.print((char)arrby[i]);
	        }
	        System.out.println();
	    }

	    public static void main(String[] arrstring) {
	    }
}