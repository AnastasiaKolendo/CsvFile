package csvfile;

public class DocumentFilterException extends Exception {
        public DocumentFilterException(String errorMessage) {
            super(errorMessage);
        }

        public DocumentFilterException(Throwable cause) {
            super(cause);
        }
}
