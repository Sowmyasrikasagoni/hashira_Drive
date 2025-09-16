import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;

public class PolynomialConstant {

    static BigInteger convert(String base, String value) {
        int b = Integer.parseInt(base);
        return new BigInteger(value, b);
    }

    
    static BigInteger lagrangeInterpolation(BigInteger[] x, BigInteger[] y, int k) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < k; i++) {
            // Start with y[i]
            BigInteger term = y[i];

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    
                    BigInteger numerator = x[j].negate();
                    BigInteger denominator = x[i].subtract(x[j]);
                 
                    term = term.multiply(numerator).divide(denominator);
                }
            }
            result = result.add(term);
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Usage: java PolynomialConstant <input.json>");
            return;
        }

        String content = new String(Files.readAllBytes(Paths.get(args[0])), StandardCharsets.UTF_8);

        // Extract n and k
        Pattern pKeys = Pattern.compile("\"keys\"\\s*:\\s*\\{[^}]*\"n\"\\s*:\\s*(\\d+),[^}]*\"k\"\\s*:\\s*(\\d+)");
        Matcher mKeys = pKeys.matcher(content);
        int n = 0, k = 0;
        if (mKeys.find()) {
            n = Integer.parseInt(mKeys.group(1));
            k = Integer.parseInt(mKeys.group(2));
        }

        // Extract base/value pairs
        Pattern pEntry = Pattern.compile("\"(\\d+)\"\\s*:\\s*\\{[^}]*\"base\"\\s*:\\s*\"(\\d+)\"\\s*,\\s*\"value\"\\s*:\\s*\"([0-9a-zA-Z]+)\"");
        Matcher mEntry = pEntry.matcher(content);

        List<BigInteger> xs = new ArrayList<>();
        List<BigInteger> ys = new ArrayList<>();

        while (mEntry.find()) {
            String key = mEntry.group(1);   // root index
            String base = mEntry.group(2);
            String value = mEntry.group(3);

            BigInteger x = new BigInteger(key);
            BigInteger y = convert(base, value);

            xs.add(x);
            ys.add(y);
        }

        if (xs.size() < k) {
            System.out.println("Not enough points to interpolate.");
            return;
        }

        // Take first k points
        BigInteger[] xArr = xs.subList(0, k).toArray(new BigInteger[0]);
        BigInteger[] yArr = ys.subList(0, k).toArray(new BigInteger[0]);

        // Find constant term
        BigInteger c = lagrangeInterpolation(xArr, yArr, k);
        System.out.println("Constant c = " + c);
    }
}
