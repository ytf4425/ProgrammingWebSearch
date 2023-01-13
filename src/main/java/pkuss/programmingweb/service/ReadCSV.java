package pkuss.programmingweb.service;

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
                // choose correct map index
                String indexString = line[1];  // for API
                if (vClass == Mashup.class) indexString = line[2];  // for Mashup

                if (map.containsKey(indexString)) {
                    V item = map.get(indexString);
                    if (item != null)
                        item.set(line);
                } else {
                    // Add missing items in the json file
                    Object[] methodArgs = new Object[]{line};
                    V item = vClass.getDeclaredConstructor(line.getClass()).newInstance(methodArgs);
                    map.put(item.getIndex(), item);
                }
            }
        } catch (IOException | CsvValidationException | NoSuchMethodException | InvocationTargetException |
                 InstantiationException | IllegalAccessException e) {
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

                if (api == null || mashup == null) continue;
                api.getMashup().add(mashup);
                mashup.getRelatedApis().add(api);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }
}
