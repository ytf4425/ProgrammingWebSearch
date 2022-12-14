package pkuss.programmingweb.dao;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import pkuss.programmingweb.entity.API;
import pkuss.programmingweb.entity.Mashup;
import pkuss.programmingweb.entity.ProgrammableWeb;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Author: SongXJ
 * Date: 2022/11/18 16:28
 * Description: 读取CSV
 */

public class ReadCSV {
    public static <V extends ProgrammableWeb> void readContent(InputStream input, Map<String, V> map, Class<V> vClass) {
        //以"\t"作为解析的分隔符
        CSVParser csvParser = new CSVParserBuilder().withSeparator('\t').build();
        try (CSVReader readerCsv = new CSVReaderBuilder(new InputStreamReader(input)).withCSVParser(csvParser).withSkipLines(1).build()) {
            String[] line;
            while ((line = readerCsv.readNext()) != null) {
                //Arrays.asList(lines).forEach(System.out::println);
                Object[] methodArgs = new Object[]{line};
                V api = vClass.getDeclaredConstructor(line.getClass()).newInstance(methodArgs);
                map.put(api.getIndex(), api);
            }
        } catch (IOException | CsvValidationException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void readM_a(InputStream input, Map<String, Mashup> Mashupmap, Map<String, API> APImap) {
        //以"\t"作为解析的分隔符
        CSVParser csvParser = new CSVParserBuilder().withSeparator('\t').build();
        try (CSVReader readerCsv = new CSVReaderBuilder(new InputStreamReader(input)).withCSVParser(csvParser).withSkipLines(1).build()) {
            String[] lines;
            while ((lines = readerCsv.readNext()) != null) {
                String mashupName = lines[0];
                String apiPath = lines[1];
                Mashup mashup = Mashupmap.get(mashupName);
                API api = APImap.get(apiPath);
                if (api != null && mashup != null) {
                    api.getMashup().add(mashup);
                    mashup.getRelatedApis().add(api);
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }
}
