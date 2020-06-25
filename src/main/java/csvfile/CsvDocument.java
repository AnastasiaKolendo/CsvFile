package csvfile;

import org.checkerframework.checker.units.qual.C;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class CsvDocument {
    @Override
    public String toString() {
        return "CsvDocument{" +
                "header=" + header +
                ", body=" + body +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CsvDocument)) return false;
        CsvDocument that = (CsvDocument) o;
        return header.equals(that.header) &&
                body.equals(that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(header, body);
    }

    final private List<HeaderCell> header;
    final private List<List<CsvValue>> body;

    public CsvDocument(List<HeaderCell> header, List<List<CsvValue>> body) {
        this.header = header;
        this.body = body;
    }

    public List<HeaderCell> getListHeader() {
        return header;
    }

    public List<List<CsvValue>> getBody() {
        return body;
    }

    public CsvDocument filter(String columnName, String filterValue) throws CsvParseException, DocumentFilterException {

        int index = -1;
        for (int i = 0; i < header.size(); i++) {
            if(header.get(i).getName().equals(columnName)){
                index = i;
                break;
            }
        }
        if(index == -1){
            throw new DocumentFilterException("The column doesn't exist");
        }
        CsvType type = header.get(index).getType();
        Object filterVal = type.parse(filterValue);
        List<List<CsvValue>> newBody = new ArrayList<>();
        for (List<CsvValue> list: body){
            Object csvValue = list.get(index).getValue();
            if(csvValue.equals(filterVal)){
                List<CsvValue> csvValues = new ArrayList<>();
                for (CsvValue value : list) {
                    csvValues.add(new CsvValue(value.getValue(), value.getRawValue()));
                }
                newBody.add(csvValues);
            }

        }
        return new CsvDocument(header, newBody);
    }

    public void exportResult(Writer out) throws IOException {
        for (int i = 0; i < header.size(); i++) {
            out.append(header.get(i).getName());
            out.append(" ");
            out.append(header.get(i).getType().getTypeName());
            if(i == header.size() - 1){
                out.append("\r\n");
            } else {
                out.append("; ");
            }
        }

        for (List<CsvValue> list : body) {
            for (int j = 0; j < list.size(); j++) {
                out.append(list.get(j).getRawValue());
                if (j < list.size() - 1) {
                    out.append(";");
                } else {
                    out.append("\r\n");
                }
            }
        }
    }
}
