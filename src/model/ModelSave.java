package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Set;

public class ModelSave {

    private String calculate(int exponent) {

        String nString = BigInteger.TWO.pow(exponent).subtract(BigInteger.ONE).toString();

        int nStringLength = nString.length();

        if (nStringLength <= 128) return nString;

        StringBuilder nStringWordWrap = new StringBuilder();

        for (int i = 0; i < nString.length(); i++) {
            if (i > 0 && i % 128 == 0) nStringWordWrap.append(System.lineSeparator());
            nStringWordWrap.append(nString.charAt(i));
        }

        return nStringWordWrap.toString();

    }

    public void save(Set<Integer> saveData) {

        File saveDir = new File("Mersenne_Prime_List");

        if (! saveDir.exists()) {
            if (! saveDir.mkdir()) return;
        }

        if (! saveDir.canWrite()) return;

        for (int exponent : saveData) {

            try {

                String fileName = "Mersenne_" + "2^" + exponent + ".TXT";

                BufferedWriter writer = new BufferedWriter(new FileWriter(new File(saveDir.getAbsolutePath() + File.separator + fileName).getAbsolutePath()));

                writer.write("Exponent: 2^" + exponent + "-1");
                writer.newLine();
                writer.newLine();
                writer.write("    :    ");
                writer.newLine();
                writer.newLine();
                writer.write(calculate(exponent));

                writer.flush();

                writer.close();

            } catch (IOException e) { throw new RuntimeException(e); }

        }

    }

}
