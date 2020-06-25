package csvfile;

import org.junit.Test;

import java.io.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class CsvParserTest {

    @Test
    public void testHeaderNoLineFit() throws Exception {
        CsvParser csvParser = new CsvParser();
        String string = "ФИО String; Дата_рождения Date; Кол-во_проектов Integer; Рейтинг Float; Комментарий String";
        InputStream inputStreamReader = new ByteArrayInputStream(string.getBytes());
        CsvDocument csvDocument = csvParser.parse(inputStreamReader, "UTF-8");
        List<HeaderCell> header1 = csvDocument.getListHeader();
        List<HeaderCell> header2 = Arrays.asList(
                new HeaderCell("ФИО", CsvType.STRING),
                new HeaderCell("Дата_рождения", CsvType.DATE),
                new HeaderCell("Кол-во_проектов", CsvType.INTEGER),
                new HeaderCell("Рейтинг", CsvType.FLOAT),
                new HeaderCell("Комментарий", CsvType.STRING)
        );
        assertEquals(header1, header2);
    }

    @Test
    public void testHeaderLineFit() throws Exception {
        CsvParser csvParser = new CsvParser();
        String string = "ФИО String; Дата_рождения Date; Кол-во_проектов Integer; Рейтинг Float; Комментарий String\r\n";
        InputStream inputStreamReader = new ByteArrayInputStream(string.getBytes());
        CsvDocument csvDocument = csvParser.parse(inputStreamReader, "UTF-8");
        List<HeaderCell> header1 = csvDocument.getListHeader();
        List<HeaderCell> header2 = Arrays.asList(
                new HeaderCell("ФИО", CsvType.STRING),
                new HeaderCell("Дата_рождения", CsvType.DATE),
                new HeaderCell("Кол-во_проектов", CsvType.INTEGER),
                new HeaderCell("Рейтинг", CsvType.FLOAT),
                new HeaderCell("Комментарий", CsvType.STRING)
        );
        assertEquals(header1, header2);
    }

    @Test
    public void testBodyNoLineFit() throws Exception {
        CsvParser csvParser = new CsvParser();
        String string = "ФИО String; Дата_рождения Date; Кол-во_проектов Integer; Рейтинг Float; Комментарий String\r\n"+
                "Иванов Иван Иванович; 18.06.1983; 34; 6,45; \"Работал над проектами:\n" +
                "1. \"\". АБС\"\"; \n" +
                "2. \"\". КВД\"\"";
        InputStream inputStreamReader = new ByteArrayInputStream(string.getBytes());
        CsvDocument csvDocument = csvParser.parse(inputStreamReader, "UTF-8");
        List<List<CsvValue>> body1 = csvDocument.getBody();
        List<CsvValue> actualLine = body1.get(0);
        List<Object> expectedLine = Arrays.asList(
                new CsvValue("Иванов Иван Иванович", "Иванов Иван Иванович"),
                new CsvValue(LocalDate.of(1983,06,18), " 18.06.1983"),
                new CsvValue(34, " 34"),
                new CsvValue(6.45, " 6,45"),
                new CsvValue(
                "Работал над проектами:\n" + "1. \". АБС\"; \n2. \". КВД\"", " \"Работал над проектами:\n" +
        "1. \"\". АБС\"\"; \n2. \"\". КВД\"\""));
        assertEquals(expectedLine, actualLine);

    }

    @Test
    public void testBodyWithLineFit() throws Exception {
        CsvParser csvParser = new CsvParser();
        String string = "ФИО String; Дата_рождения Date; Кол-во_проектов Integer; Рейтинг Float; Комментарий String\r\n"+
                "Иванов Иван Иванович; 18.06.1983; 34; 6,45; Работал над проектами: АБС, КВД\r\n";
        InputStream inputStreamReader = new ByteArrayInputStream(string.getBytes());
        CsvDocument csvDocument = csvParser.parse(inputStreamReader, "UTF-8");
        List<List<CsvValue>> body1 = csvDocument.getBody();
        List<CsvValue> actualLine = body1.get(0);
        List<Object> expectedLine = Arrays.asList(
                new CsvValue("Иванов Иван Иванович", "Иванов Иван Иванович"),
                new CsvValue(LocalDate.of(1983,06,18), " 18.06.1983"),
                new CsvValue(34, " 34"),
                new CsvValue(6.45, " 6,45"),
                new CsvValue("Работал над проектами: АБС, КВД", " Работал над проектами: АБС, КВД\r\n"));
        assertEquals(expectedLine, actualLine);
    }

    @Test(expected = CsvParseException.class)
    public void testIllegalType() throws Exception {
        CsvParser csvParser = new CsvParser();
        String string = "ФИО Pipa; Дата_рождения Date; Кол-во_проектов Integer; Рейтинг Float; Комментарий String\r\n"+
                "Иванов Иван Иванович; 18.06.1983; 34; 6,45; Работал над проектами: АБС, КВД\r\n";
        InputStream inputStreamReader = new ByteArrayInputStream(string.getBytes());
        csvParser.parse(inputStreamReader, "UTF-8");
    }

    @Test(expected = CsvParseException.class)
    public void testDateTypeFormatValidation() throws Exception {
        CsvParser csvParser = new CsvParser();
        String string = "ФИО String; Дата_рождения Date; Кол-во_проектов Integer; Рейтинг Float; Комментарий String\r\n"+
                "Иванов Иван Иванович; pipa; 34; 6,45; Работал над проектами: АБС, КВД\r\n";
        InputStream inputStreamReader = new ByteArrayInputStream(string.getBytes());
        csvParser.parse(inputStreamReader, "UTF-8");
    }

    @Test(expected = CsvParseException.class)
    public void testFloatTypeFormatValidation() throws Exception {
        CsvParser csvParser = new CsvParser();
        String string = "ФИО String; Дата_рождения Date; Кол-во_проектов Integer; Рейтинг Float; Комментарий String\r\n"+
                "Иванов Иван Иванович; 18.06.1983; 34; pipa; Работал над проектами: АБС, КВД\r\n";
        InputStream inputStreamReader = new ByteArrayInputStream(string.getBytes());
        csvParser.parse(inputStreamReader, "UTF-8");
    }

    @Test(expected = CsvParseException.class)
    public void testIntegerTypeFormatValidation() throws Exception {
        CsvParser csvParser = new CsvParser();
        String string = "ФИО String; Дата_рождения Date; Кол-во_проектов Integer; Рейтинг Float; Комментарий String\r\n"+
                "Иванов Иван Иванович; 18.06.1983; pipa; 6,45; Работал над проектами: АБС, КВД\r\n";
        InputStream inputStreamReader = new ByteArrayInputStream(string.getBytes());
        csvParser.parse(inputStreamReader, "UTF-8");
    }


    @Test(expected = CsvParseException.class)
    public void notAllColumnsInLine() throws Exception{
        CsvParser csvParser = new CsvParser();
        String string = "ФИО String; Дата_рождения Date; Кол-во_проектов Integer; Рейтинг Float; Комментарий String\r\n"+
                "Иванов Иван Иванович; 18.06.1983; 34; 6,45\r\n";
        InputStream inputStreamReader = new ByteArrayInputStream(string.getBytes());
        csvParser.parse(inputStreamReader, "UTF-8");
    }

    @Test(expected = CsvParseException.class)
    public void notAllColumnsInLineNoLineFit() throws Exception{
        CsvParser csvParser = new CsvParser();
        String string = "ФИО String; Дата_рождения Date; Кол-во_проектов Integer; Рейтинг Float; Комментарий String\r\n"+
                "Иванов Иван Иванович; 18.06.1983; 34; 6,45";
        InputStream inputStreamReader = new ByteArrayInputStream(string.getBytes());
        csvParser.parse(inputStreamReader, "UTF-8");
    }

    @Test(expected = CsvParseException.class)
    public void moreColumnsInRow() throws Exception{
        CsvParser csvParser = new CsvParser();
        String string = "ФИО String; Дата_рождения Date; Кол-во_проектов Integer; Рейтинг Float; Комментарий String\r\n"+
                "Иванов Иван Иванович; 18.06.1983; 34; 6,45; Работал над проектами: \"\"АБС\"\"; \n" +
                "2 ””. КВД”””; pina\r\n";
        InputStream inputStreamReader = new ByteArrayInputStream(string.getBytes());
        csvParser.parse(inputStreamReader, "UTF-8");
    }
}
