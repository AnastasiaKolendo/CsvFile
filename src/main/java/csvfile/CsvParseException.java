package csvfile;

class CsvParseException extends Exception {
    public CsvParseException(String errorMessage) {
        super(errorMessage);
    }

    public CsvParseException(Throwable cause) {
        super(cause);
    }
}
