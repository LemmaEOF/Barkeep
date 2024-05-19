package gay.lemmaeof.barkeep.util;

public class TextUtils {

    public static String getPartNumber(int quarters) {
        int whole = quarters / 4;
        String fraction = switch(quarters % 4) {
            case 1 -> "¼";
            case 2 -> "½";
            case 3 -> "¾";
            default -> "";
        };
        return whole > 0? whole + fraction : fraction;
    }

}
