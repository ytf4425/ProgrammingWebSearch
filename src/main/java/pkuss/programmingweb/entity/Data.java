package pkuss.programmingweb.entity;

import org.springframework.core.io.ClassPathResource;
import pkuss.programmingweb.dao.ReadCSV;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@lombok.Data
public class Data {
    InvertedIndex ii;
    Map<String, API> APImap = new HashMap<>();
    Map<String, Mashup> Mashupmap = new HashMap<>();
    private InputStream Api_file_path = new ClassPathResource("static/api_nodes_estimator.csv").getInputStream();
    private InputStream Mashup_file_path = new ClassPathResource("static/mashup_nodes_estimator.csv").getInputStream();
    private InputStream M_A_file_path = new ClassPathResource("static/m-a_edges.csv").getInputStream();

    public Data(InvertedIndex ii) throws IOException {
        this.ii = ii;
        initData();
    }

    private void initData() {
        ReadCSV.readContent(getApi_file_path(), getAPImap(), API.class);
        ReadCSV.readContent(getMashup_file_path(), getMashupmap(), Mashup.class);
        ReadCSV.readM_a(getM_A_file_path(), getMashupmap(), getAPImap());

        for (API api : getAPImap().values()) {
            // add API basic information to inverted index
            String apiCategory = api.getCategory();
            ii.setMap(apiCategory.toLowerCase(), api, type.API_CATEGORY);

            String apiName = api.getName();
            for (String word : apiName.split(" "))
                ii.setMap(word.toLowerCase(), api, type.API_NAME);

            // add related Mashup App information to inverted index
            for (Mashup mashup : api.getMashup()) {
                String mashupCategory = mashup.getCategory();
                ii.setMap(mashupCategory.toLowerCase(), api, type.MASHUP_CATEGORY);
                String mashupName = mashup.getName();
                //System.out.println(mashupName);
                ii.setMap(mashupName.toLowerCase(), api, type.MASHUP_NAME);
            }
        }
    }
}
