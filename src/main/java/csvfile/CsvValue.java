package csvfile;

import java.util.Objects;

class CsvValue {
    private final Object value;
    private final String rawValue;

    CsvValue(Object value, String rawValue) {
        this.value = value;
        this.rawValue = rawValue;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CsvValue that = (CsvValue) o;
        return Objects.equals(value, that.value) &&
                rawValue.equals(that.rawValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, rawValue);
    }

    public Object getValue() {
        return value;
    }

    public String getRawValue() {
        return rawValue;
    }

    @Override
    public String toString() {
        return "CsvValue{" +
                "value=" + value +
                ", rawValue='" + rawValue + '\'' +
                '}';
    }

}
