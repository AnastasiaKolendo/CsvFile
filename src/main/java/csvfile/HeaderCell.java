package csvfile;

import java.util.Objects;

class HeaderCell {
    private final String name;
    private final CsvType type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HeaderCell that = (HeaderCell) o;
        return Objects.equals(name, that.name) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public String toString() {
        return "HeaderCell{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }

    public HeaderCell(String name, CsvType type) {
        this.name = name;
        this.type = type;
    }

    public CsvType getType() {
        return type;
    }
    public String getName(){return name; }
}
