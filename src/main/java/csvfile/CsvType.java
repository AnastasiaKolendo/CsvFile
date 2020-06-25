package csvfile;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

enum CsvType {
    STRING ("String"){
        @Override
        public Object parse(String s) {
            return s;
        }
    }, DATE ("Date") {
        @Override
        public Object parse(String s) throws CsvParseException {
            try {
                return LocalDate.parse(s, DateTimeFormatter.ofPattern("dd.M.yyyy"));
            } catch (DateTimeParseException e) {
                throw new CsvParseException(e);
            }
        }
    }, INTEGER ("Integer"){
        @Override
        public Object parse(String s) throws CsvParseException {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                throw new CsvParseException(e);
            }
        }
    }, FLOAT ("Float"){
        @Override
        public Object parse(String s) throws CsvParseException {
            try {
                DecimalFormat df = new DecimalFormat();
                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setDecimalSeparator(',');
                df.setDecimalFormatSymbols(symbols);
                return df.parse(s).doubleValue();
            } catch (ParseException e) {
                throw new CsvParseException(e);
            }
        }
    };

    private final String value;
    CsvType(String value){
        this.value = value;
    }

    public String getTypeName() {
        return this.value;
    }

    public abstract Object parse(String s) throws CsvParseException;

    public static CsvType fromString(String typeString) throws CsvParseException {
        switch (typeString) {
            case "String":
                return CsvType.STRING;
            case "Date":
                return CsvType.DATE;
            case "Integer":
                return CsvType.INTEGER;
            case "Float":
                return CsvType.FLOAT;
            default:
                throw new CsvParseException("Unexpected type " + typeString);
        }
    }
}
