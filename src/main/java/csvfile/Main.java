package csvfile;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException, CsvParseException, DocumentFilterException {
        String strings = Arrays.toString(args);
        String in = "";
        String out = "";
        String encoding = "";
        String columnName = "";
        String expressionCoding = "";

        for (int i = 0; i < args.length; i = i + 2) {
            String key = args[i];
            switch (key) {
                case "–in":
                    in = args[i + 1];
                    break;
                case "–out":
                    out = args[i + 1];
                    break;
                case "-enc":
                    encoding = args[i + 1];
                    break;
                case "-col":
                    columnName = args[i + 1];
                    break;
                case "-exp":
                    expressionCoding = args[i + 1];
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected parameter " + key);
            }
        }
        if (in == null) {
            throw new IllegalArgumentException("Enter input file");
        }
        if (out == null) {
            throw new IllegalArgumentException("Enter output file");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Enter encoding");
        }
        if (columnName == null) {
            throw new IllegalArgumentException("Enter column name");
        }
        if (expressionCoding == null) {
            throw new IllegalArgumentException("Enter expression");
        }

        CsvDocument csvDocument;
        try(FileInputStream fileInputStream = new FileInputStream(in)) {
            csvDocument = new CsvParser().parse(fileInputStream, encoding);
        }

        CsvDocument newCsvDocument = csvDocument.filter(columnName, expressionCoding);
        try(Writer writer = new FileWriter(out, Charset.forName(encoding))) {
            newCsvDocument.exportResult(writer);
        }
    }
}
