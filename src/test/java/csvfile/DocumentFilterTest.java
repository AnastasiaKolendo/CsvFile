package csvfile;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DocumentFilterTest {

    @Test
    public void testExport() throws DocumentFilterException, CsvParseException, IOException {
        List<HeaderCell> header = Arrays.asList(
                new HeaderCell("ФИО", CsvType.STRING),
                new HeaderCell("Дата_рождения", CsvType.DATE),
                new HeaderCell("Кол-во_проектов", CsvType.INTEGER),
                new HeaderCell("Рейтинг", CsvType.FLOAT),
                new HeaderCell("Комментарий", CsvType.STRING)
        );


        List<List<CsvValue>> body = new ArrayList<>();
        List<CsvValue> line1 = Arrays.asList(
                new CsvValue("Иванов Иван Иванович", "Иванов Иван Иванович"),
                new CsvValue(LocalDate.of(1983,06,18), " 18.06.1983"),
                new CsvValue(34, " 34"),
                new CsvValue(6.45, " 6,45"),
                new CsvValue(
                        "Работал над проектами:\n" + "1. \". АБС\"; \n2. \". КВД\"", " \"Работал над проектами:\n" +
                        "1. \"\". АБС\"\"; \n2. \"\". КВД\"\""));

        body.add(line1);
        List<CsvValue> line2 = Arrays.asList(
                new CsvValue("John Snow", "John Snow"),
                new CsvValue(LocalDate.of(2000,07,30), " 30.07.2000"),
                new CsvValue(34, " 34"),
                new CsvValue(7.45, " 7,45"),
                new CsvValue("You know nothing",
                        " You know nothing"));
        body.add(line2);

        List<CsvValue> line3 = Arrays.asList(
                new CsvValue("Piny", "Piny"),
                new CsvValue(LocalDate.of(1987,04,02), " 02.04.1987"),
                new CsvValue(0, " 0"),
                new CsvValue(7.01, " 7,01"),
                new CsvValue("You are a poop",
                        " You are a poop"));
        body.add(line3);
        CsvDocument csvDocument = new CsvDocument(header,body);

        CsvDocument newDocument = csvDocument.filter("Кол-во_проектов", "34");

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(bout);
        newDocument.exportResult(osw);
        osw.flush();
        String result = new String(bout.toByteArray());
        String expected = "ФИО String; Дата_рождения Date; Кол-во_проектов Integer; Рейтинг Float; Комментарий String\r\n" +
                "Иванов Иван Иванович; 18.06.1983; 34; 6,45; \"Работал над проектами:\n" +
                "1. \"\". АБС\"\"; \n2. \"\". КВД\"\"\r\n" +
                "John Snow; 30.07.2000; 34; 7,45; You know nothing\r\n";
        assertEquals(expected, result);
    }

    @Test
    public void filerByIntegerFilter() throws CsvParseException, DocumentFilterException {
        List<HeaderCell> header = Arrays.asList(
                new HeaderCell("ФИО", CsvType.STRING),
                new HeaderCell("Дата_рождения", CsvType.DATE),
                new HeaderCell("Кол-во_проектов", CsvType.INTEGER),
                new HeaderCell("Рейтинг", CsvType.FLOAT),
                new HeaderCell("Комментарий", CsvType.STRING)
        );


        List<List<CsvValue>> body = new ArrayList<>();
        List<CsvValue> line1 = Arrays.asList(
                new CsvValue("Иванов Иван Иванович", "Иванов Иван Иванович"),
                new CsvValue(LocalDate.of(1983,06,18), " 18.06.1983"),
                new CsvValue(34, " 34"),
                new CsvValue(6.45, " 6,45"),
                new CsvValue(
                        "Работал над проектами:\n" + "1. \". АБС\"; \n2. \". КВД\"", " \"Работал над проектами:\n" +
                        "1. \"\". АБС\"\"; \n2. \"\". КВД\"\""));

        body.add(line1);
        List<CsvValue> line2 = Arrays.asList(
                new CsvValue("John Snow", "John Snow"),
                new CsvValue(LocalDate.of(2000,07,30), " 30.07.2000"),
                new CsvValue(100, " 100"),
                new CsvValue(7.45, " 7,45"),
                new CsvValue("You know nothing",
                        " You know nothing"));
        body.add(line2);

        List<CsvValue> line3 = Arrays.asList(
                new CsvValue("Piny", "Piny"),
                new CsvValue(LocalDate.of(1987,04,02), " 02.04.1987"),
                new CsvValue(0, " 0"),
                new CsvValue(7.01, " 7,01"),
                new CsvValue("You are a poop",
                        " You are a poop"));
        body.add(line3);


        List<CsvValue> line4 = Arrays.asList(
                new CsvValue("Piny", "Darcy"),
                new CsvValue(LocalDate.of(2014,01,25), " 25.01.2014"),
                new CsvValue(0, " 0"),
                new CsvValue(7.01, " 7,01"),
                new CsvValue("Get off the table",
                        " Get off the table"));
        body.add(line4);
        CsvDocument csvDocument = new CsvDocument(header,body);

        CsvDocument newDocument = csvDocument.filter("Кол-во_проектов", "0");

        List<List<CsvValue>> newBody = new ArrayList<>();
        List<CsvValue> lineNew1 = Arrays.asList(
                new CsvValue("Piny", "Piny"),
                new CsvValue(LocalDate.of(1987,04,02), " 02.04.1987"),
                new CsvValue(0, " 0"),
                new CsvValue(7.01, " 7,01"),
                new CsvValue("You are a poop",
                        " You are a poop"));
        newBody.add(lineNew1);

        List<CsvValue> lineNew2 = Arrays.asList(
                new CsvValue("Piny", "Darcy"),
                new CsvValue(LocalDate.of(2014,01,25), " 25.01.2014"),
                new CsvValue(0, " 0"),
                new CsvValue(7.01, " 7,01"),
                new CsvValue("Get off the table",
                        " Get off the table"));
        newBody.add(lineNew2);

        CsvDocument expectedDocument = new CsvDocument(header, newBody);

        assertEquals(expectedDocument, newDocument);
    }

    @Test
    public void filerByStringFilter() throws CsvParseException, DocumentFilterException {
        List<HeaderCell> header = Arrays.asList(
                new HeaderCell("ФИО", CsvType.STRING),
                new HeaderCell("Дата_рождения", CsvType.DATE),
                new HeaderCell("Кол-во_проектов", CsvType.INTEGER),
                new HeaderCell("Рейтинг", CsvType.FLOAT),
                new HeaderCell("Комментарий", CsvType.STRING)
        );


        List<List<CsvValue>> body = new ArrayList<>();
        List<CsvValue> line1 = Arrays.asList(
                new CsvValue("Иванов Иван Иванович", "Иванов Иван Иванович"),
                new CsvValue(LocalDate.of(1983,06,18), " 18.06.1983"),
                new CsvValue(34, " 34"),
                new CsvValue(6.45, " 6,45"),
                new CsvValue(
                        "Работал над проектами:\n" + "1. \". АБС\"; \n2. \". КВД\"", " \"Работал над проектами:\n" +
                        "1. \"\". АБС\"\"; \n2. \"\". КВД\"\""));

        body.add(line1);
        List<CsvValue> line2 = Arrays.asList(
                new CsvValue("John Snow", "John Snow"),
                new CsvValue(LocalDate.of(2000,07,30), " 30.07.2000"),
                new CsvValue(100, " 100"),
                new CsvValue(7.45, " 7,45"),
                new CsvValue("You know nothing",
                        " You know nothing"));
        body.add(line2);

        List<CsvValue> line3 = Arrays.asList(
                new CsvValue("Piny", "Piny"),
                new CsvValue(LocalDate.of(1987,04,02), " 02.04.1987"),
                new CsvValue(56, " 56"),
                new CsvValue(7.01, " 7,01"),
                new CsvValue("You are a poop",
                        " You are a poop"));
        body.add(line3);


        List<CsvValue> line4 = Arrays.asList(
                new CsvValue("Иванов Иван Иванович", "Иванов Иван Иванович"),
                new CsvValue(LocalDate.of(2014,01,25), " 25.01.2014"),
                new CsvValue(0, " 0"),
                new CsvValue(7.01, " 7,01"),
                new CsvValue("Get off the table",
                        " Get off the table"));
        body.add(line4);
        CsvDocument csvDocument = new CsvDocument(header,body);

        CsvDocument newDocument = csvDocument.filter("ФИО", "Иванов Иван Иванович");

        List<List<CsvValue>> newBody = new ArrayList<>();
        List<CsvValue> lineNew1 = Arrays.asList(
                new CsvValue("Иванов Иван Иванович", "Иванов Иван Иванович"),
                new CsvValue(LocalDate.of(1983,06,18), " 18.06.1983"),
                new CsvValue(34, " 34"),
                new CsvValue(6.45, " 6,45"),
                new CsvValue(
                        "Работал над проектами:\n" + "1. \". АБС\"; \n2. \". КВД\"", " \"Работал над проектами:\n" +
                        "1. \"\". АБС\"\"; \n2. \"\". КВД\"\""));
        newBody.add(lineNew1);

        List<CsvValue> lineNew2 = Arrays.asList(
                new CsvValue("Иванов Иван Иванович", "Иванов Иван Иванович"),
                new CsvValue(LocalDate.of(2014,01,25), " 25.01.2014"),
                new CsvValue(0, " 0"),
                new CsvValue(7.01, " 7,01"),
                new CsvValue("Get off the table",
                        " Get off the table"));
        newBody.add(lineNew2);

        CsvDocument expectedDocument = new CsvDocument(header, newBody);

        assertEquals(expectedDocument, newDocument);
    }

    @Test
    public void filerByDateFilter() throws CsvParseException, DocumentFilterException {
        List<HeaderCell> header = Arrays.asList(
                new HeaderCell("ФИО", CsvType.STRING),
                new HeaderCell("Дата_рождения", CsvType.DATE),
                new HeaderCell("Кол-во_проектов", CsvType.INTEGER),
                new HeaderCell("Рейтинг", CsvType.FLOAT),
                new HeaderCell("Комментарий", CsvType.STRING)
        );


        List<List<CsvValue>> body = new ArrayList<>();
        List<CsvValue> line1 = Arrays.asList(
                new CsvValue("Иванов Иван Иванович", "Иванов Иван Иванович"),
                new CsvValue(LocalDate.of(1983,06,18), " 18.06.1983"),
                new CsvValue(34, " 34"),
                new CsvValue(6.45, " 6,45"),
                new CsvValue(
                        "Работал над проектами:\n" + "1. \". АБС\"; \n2. \". КВД\"", " \"Работал над проектами:\n" +
                        "1. \"\". АБС\"\"; \n2. \"\". КВД\"\""));

        body.add(line1);
        List<CsvValue> line2 = Arrays.asList(
                new CsvValue("John Snow", "John Snow"),
                new CsvValue(LocalDate.of(1983,06,18), " 18.06.1983"),
                new CsvValue(100, " 100"),
                new CsvValue(7.45, " 7,45"),
                new CsvValue("You know nothing",
                        " You know nothing"));
        body.add(line2);

        List<CsvValue> line3 = Arrays.asList(
                new CsvValue("Piny", "Piny"),
                new CsvValue(LocalDate.of(1987,04,02), " 02.04.1987"),
                new CsvValue(56, " 56"),
                new CsvValue(7.01, " 7,01"),
                new CsvValue("You are a poop",
                        " You are a poop"));
        body.add(line3);


        List<CsvValue> line4 = Arrays.asList(
                new CsvValue("Иванов Иван Иванович", "Иванов Иван Иванович"),
                new CsvValue(LocalDate.of(2014,01,25), " 25.01.2014"),
                new CsvValue(0, " 0"),
                new CsvValue(7.01, " 7,01"),
                new CsvValue("Get off the table",
                        " Get off the table"));
        body.add(line4);
        CsvDocument csvDocument = new CsvDocument(header,body);

        CsvDocument newDocument = csvDocument.filter("Дата_рождения", "18.06.1983");

        List<List<CsvValue>> newBody = new ArrayList<>();
        List<CsvValue> lineNew1 = Arrays.asList(
                new CsvValue("Иванов Иван Иванович", "Иванов Иван Иванович"),
                new CsvValue(LocalDate.of(1983,06,18), " 18.06.1983"),
                new CsvValue(34, " 34"),
                new CsvValue(6.45, " 6,45"),
                new CsvValue(
                        "Работал над проектами:\n" + "1. \". АБС\"; \n2. \". КВД\"", " \"Работал над проектами:\n" +
                        "1. \"\". АБС\"\"; \n2. \"\". КВД\"\""));
        newBody.add(lineNew1);

        List<CsvValue> lineNew2 = Arrays.asList(
                new CsvValue("John Snow", "John Snow"),
                new CsvValue(LocalDate.of(1983,06,18), " 18.06.1983"),
                new CsvValue(100, " 100"),
                new CsvValue(7.45, " 7,45"),
                new CsvValue("You know nothing",
                        " You know nothing"));
        newBody.add(lineNew2);

        CsvDocument expectedDocument = new CsvDocument(header, newBody);

        assertEquals(expectedDocument, newDocument);
    }


    @Test
    public void filerByFloatFilter() throws CsvParseException, DocumentFilterException {
        List<HeaderCell> header = Arrays.asList(
                new HeaderCell("ФИО", CsvType.STRING),
                new HeaderCell("Дата_рождения", CsvType.DATE),
                new HeaderCell("Кол-во_проектов", CsvType.INTEGER),
                new HeaderCell("Рейтинг", CsvType.FLOAT),
                new HeaderCell("Комментарий", CsvType.STRING)
        );


        List<List<CsvValue>> body = new ArrayList<>();
        List<CsvValue> line1 = Arrays.asList(
                new CsvValue("Иванов Иван Иванович", "Иванов Иван Иванович"),
                new CsvValue(LocalDate.of(1983,06,18), " 18.06.1983"),
                new CsvValue(34, " 34"),
                new CsvValue(6.45, " 6,45"),
                new CsvValue(
                        "Работал над проектами:\n" + "1. \". АБС\"; \n2. \". КВД\"", " \"Работал над проектами:\n" +
                        "1. \"\". АБС\"\"; \n2. \"\". КВД\"\""));

        body.add(line1);
        List<CsvValue> line2 = Arrays.asList(
                new CsvValue("John Snow", "John Snow"),
                new CsvValue(LocalDate.of(2000,07,30), " 30.07.2000"),
                new CsvValue(100, " 100"),
                new CsvValue(7.45, " 7,45"),
                new CsvValue("You know nothing",
                        " You know nothing"));
        body.add(line2);

        List<CsvValue> line3 = Arrays.asList(
                new CsvValue("Piny", "Piny"),
                new CsvValue(LocalDate.of(1987,04,02), " 02.04.1987"),
                new CsvValue(56, " 56"),
                new CsvValue(6.45, " 6,45"),
                new CsvValue("You are a poop",
                        " You are a poop"));
        body.add(line3);


        List<CsvValue> line4 = Arrays.asList(
                new CsvValue("Иванов Иван Иванович", "Иванов Иван Иванович"),
                new CsvValue(LocalDate.of(2014,01,25), " 25.01.2014"),
                new CsvValue(0, " 0"),
                new CsvValue(7.01, " 7,01"),
                new CsvValue("Get off the table",
                        " Get off the table"));
        body.add(line4);
        CsvDocument csvDocument = new CsvDocument(header,body);

        CsvDocument newDocument = csvDocument.filter("Рейтинг", "6,45");

        List<List<CsvValue>> newBody = new ArrayList<>();
        List<CsvValue> lineNew1 = Arrays.asList(
                new CsvValue("Иванов Иван Иванович", "Иванов Иван Иванович"),
                new CsvValue(LocalDate.of(1983,06,18), " 18.06.1983"),
                new CsvValue(34, " 34"),
                new CsvValue(6.45, " 6,45"),
                new CsvValue(
                        "Работал над проектами:\n" + "1. \". АБС\"; \n2. \". КВД\"", " \"Работал над проектами:\n" +
                        "1. \"\". АБС\"\"; \n2. \"\". КВД\"\""));
        newBody.add(lineNew1);

        List<CsvValue> lineNew2 = Arrays.asList(
                new CsvValue("Piny", "Piny"),
                new CsvValue(LocalDate.of(1987,04,02), " 02.04.1987"),
                new CsvValue(56, " 56"),
                new CsvValue(6.45, " 6,45"),
                new CsvValue("You are a poop",
                        " You are a poop"));
        newBody.add(lineNew2);

        CsvDocument expectedDocument = new CsvDocument(header, newBody);

        assertEquals(expectedDocument, newDocument);
    }

    @Test
    public void filerBySTRINGFilter() throws CsvParseException, DocumentFilterException {
        List<HeaderCell> header = Arrays.asList(
                new HeaderCell("ФИО", CsvType.STRING),
                new HeaderCell("Дата_рождения", CsvType.DATE),
                new HeaderCell("Кол-во_проектов", CsvType.INTEGER),
                new HeaderCell("Рейтинг", CsvType.FLOAT),
                new HeaderCell("Комментарий", CsvType.STRING)
        );


        List<List<CsvValue>> body = new ArrayList<>();
        List<CsvValue> line1 = Arrays.asList(
                new CsvValue("Иванов Иван Иванович", "Иванов Иван Иванович"),
                new CsvValue(LocalDate.of(1983,06,18), " 18.06.1983"),
                new CsvValue(34, " 34"),
                new CsvValue(6.45, " 6,45"),
                new CsvValue("Get off the table",
                        " Get off the table"));

        body.add(line1);
        List<CsvValue> line2 = Arrays.asList(
                new CsvValue("John Snow", "John Snow"),
                new CsvValue(LocalDate.of(2000,07,30), " 30.07.2000"),
                new CsvValue(100, " 100"),
                new CsvValue(7.45, " 7,45"),
                new CsvValue("You know nothing",
                        " You know nothing"));
        body.add(line2);

        List<CsvValue> line3 = Arrays.asList(
                new CsvValue("Piny", "Piny"),
                new CsvValue(LocalDate.of(1987,04,02), " 02.04.1987"),
                new CsvValue(56, " 56"),
                new CsvValue(6.45, " 6,45"),
                new CsvValue("You are a poop",
                        " You are a poop"));
        body.add(line3);


        List<CsvValue> line4 = Arrays.asList(
                new CsvValue("Иванов Иван Иванович", "Иванов Иван Иванович"),
                new CsvValue(LocalDate.of(2014,01,25), " 25.01.2014"),
                new CsvValue(0, " 0"),
                new CsvValue(7.01, " 7,01"),
                new CsvValue("Get off the table",
                        " Get off the table"));
        body.add(line4);
        CsvDocument csvDocument = new CsvDocument(header,body);

        CsvDocument newDocument = csvDocument.filter("Комментарий", "Get off the table");

        List<List<CsvValue>> newBody = new ArrayList<>();
        List<CsvValue> lineNew1 = Arrays.asList(
                new CsvValue("Иванов Иван Иванович", "Иванов Иван Иванович"),
                new CsvValue(LocalDate.of(1983,06,18), " 18.06.1983"),
                new CsvValue(34, " 34"),
                new CsvValue(6.45, " 6,45"),
                new CsvValue("Get off the table",
                        " Get off the table"));
        newBody.add(lineNew1);

        List<CsvValue> lineNew2 = Arrays.asList(
                new CsvValue("Иванов Иван Иванович", "Иванов Иван Иванович"),
                new CsvValue(LocalDate.of(2014,01,25), " 25.01.2014"),
                new CsvValue(0, " 0"),
                new CsvValue(7.01, " 7,01"),
                new CsvValue("Get off the table",
                        " Get off the table"));
        newBody.add(lineNew2);

        CsvDocument expectedDocument = new CsvDocument(header, newBody);

        assertEquals(expectedDocument, newDocument);
    }

    @Test(expected = DocumentFilterException.class)
    public void test() throws CsvParseException, DocumentFilterException {
        List<HeaderCell> header = Arrays.asList(
                new HeaderCell("ФИО", CsvType.STRING),
                new HeaderCell("Дата_рождения", CsvType.DATE),
                new HeaderCell("Кол-во_проектов", CsvType.INTEGER),
                new HeaderCell("Рейтинг", CsvType.FLOAT),
                new HeaderCell("Комментарий", CsvType.STRING)
        );


        List<List<CsvValue>> body = new ArrayList<>();
        List<CsvValue> line1 = Arrays.asList(
                new CsvValue("Иванов Иван Иванович", "Иванов Иван Иванович"),
                new CsvValue(LocalDate.of(1983,06,18), " 18.06.1983"),
                new CsvValue(34, " 34"),
                new CsvValue(6.45, " 6,45"),
                new CsvValue("Get off the table",
                        " Get off the table"));
        body.add(line1);

        List<CsvValue> line2 = Arrays.asList(
                new CsvValue("John Snow", "John Snow"),
                new CsvValue(LocalDate.of(2000,07,30), " 30.07.2000"),
                new CsvValue(100, " 100"),
                new CsvValue(7.45, " 7,45"),
                new CsvValue("You know nothing",
                        " You know nothing"));
        body.add(line2);

        CsvDocument csvDocument = new CsvDocument(header,body);
        csvDocument.filter("Comment", "Piss off ");
    }

    @Test(expected = CsvParseException.class)
    public void testIncorrectFilterValueFormat() throws CsvParseException, DocumentFilterException {
        {
            List<HeaderCell> header = Arrays.asList(
                    new HeaderCell("ФИО", CsvType.STRING),
                    new HeaderCell("Дата_рождения", CsvType.DATE),
                    new HeaderCell("Кол-во_проектов", CsvType.INTEGER),
                    new HeaderCell("Рейтинг", CsvType.FLOAT),
                    new HeaderCell("Комментарий", CsvType.STRING)
            );


            List<List<CsvValue>> body = new ArrayList<>();
            List<CsvValue> line1 = Arrays.asList(
                    new CsvValue("Иванов Иван Иванович", "Иванов Иван Иванович"),
                    new CsvValue(LocalDate.of(1983, 06, 18), " 18.06.1983"),
                    new CsvValue(34, " 34"),
                    new CsvValue(6.45, " 6,45"),
                    new CsvValue("Get off the table",
                            " Get off the table"));
            body.add(line1);

            List<CsvValue> line2 = Arrays.asList(
                    new CsvValue("John Snow", "John Snow"),
                    new CsvValue(LocalDate.of(2000, 07, 30), " 30.07.2000"),
                    new CsvValue(100, " 100"),
                    new CsvValue(7.45, " 7,45"),
                    new CsvValue("You know nothing",
                            " You know nothing"));
            body.add(line2);

            CsvDocument csvDocument = new CsvDocument(header, body);
            csvDocument.filter("Дата_рождения", "Piss off ");
        }

    }
}
