package csvfile;


import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CsvParser {
    public CsvDocument parse(InputStream in, String encoding) throws IOException, CsvParseException {
        Reader reader = new InputStreamReader(in, encoding);
        Reader buffer = new BufferedReader(reader);
        List<HeaderCell> header = parseHeader(buffer);
        List<List<CsvValue>> body = parseBody(buffer, header);
        return new CsvDocument(header, body);
    }

    private List<List<CsvValue>> parseBody(Reader reader, List<HeaderCell> header) throws IOException, CsvParseException {
        List<List<CsvValue>> body = new ArrayList<>();
        while (true) {
            List<CsvValue> line = parseLine(reader, header);
            if (line == null) {
                break;
            }
            body.add(line);
        }
        return body;
    }

    private List<CsvValue> parseLine(Reader reader, List<HeaderCell> header) throws CsvParseException, IOException {
        Integer next = reader.read();
        if (next == -1) {
            return null;
        }
        List<CsvValue> row = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();
        StringBuilder rawValue = new StringBuilder();
        while (true) {
            if (next == null) {
                next = reader.read();
            }
            if (next == -1) {

                addObject(header, row, sb, rawValue);
                if (row.size() != header.size()) {
                    throw new CsvParseException("Expected " + header.size() + " columns, got " + row.size());
                }
                return row;
            }
            char c = (char) next.intValue();
            next = null;
            switch (c) {
                case '"':
                    rawValue.append('"');
                    next = reader.read();
                    if (next == '"') {
                        sb.append('"');
                        rawValue.append('"');
                        next = null;
                    } else {
                        inQuotes = !inQuotes;
                    }
                    break;
                case ';':
                    if (inQuotes) {
                        sb.append(';');
                        rawValue.append(';');
                    } else {
                        addObject(header, row, sb, rawValue);
                        sb = new StringBuilder();
                        rawValue = new StringBuilder();
                    }
                    break;
                case '\r':
                    if (inQuotes) {
                        rawValue.append('\r');
                        sb.append('\r');
                    } else {
                        next = reader.read();
                        if (next == '\n') {
                            rawValue.append('\r');
                            rawValue.append('\n');
                            addObject(header, row, sb, rawValue);
                            if (row.size() != header.size()) {
                                throw new CsvParseException("Expected " + header.size() + " columns, got " + row.size());
                            }
                            return row;
                        }
                    }
                default:
                    sb.append(c);
                    rawValue.append(c);
            }
        }
    }

    private void addObject(List<HeaderCell> header, List<CsvValue> row, StringBuilder sb, StringBuilder rawValue) throws CsvParseException {
        if (row.size() >= header.size()) {
            throw new CsvParseException("Too many columns. Expected " + header.size());
        }
        String s = sb.toString().trim();
        HeaderCell headerCell = header.get(row.size());
        CsvType type = headerCell.getType();
        row.add(new CsvValue(type.parse(s), rawValue.toString()));
    }


    private List<HeaderCell> parseHeader(Reader reader) throws IOException, CsvParseException {
        List<HeaderCell> header = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        while (true) {
            int r = reader.read();
            if (r == -1) {
                break;
            }
            char character = (char) r;
            if (character == '\r') {
                if ((reader.read()) == '\n') {
                    break;
                } else {
                    stringBuilder.append(character);
                }
            } else {
                stringBuilder.append(character);
            }
        }
        String line = stringBuilder.toString();

        String[] strings = line.split(";");
        for (String string : strings) {
            String[] cells = string.trim().split("\\s+");
            String typeString = cells[1].trim();
            CsvType type = CsvType.fromString(typeString);
            header.add(new HeaderCell(cells[0], type));
        }
        return header;
    }
}

