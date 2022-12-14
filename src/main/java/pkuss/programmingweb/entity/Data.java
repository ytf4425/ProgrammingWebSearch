package pkuss.programmingweb.entity;

import org.springframework.core.io.ClassPathResource;
import pkuss.programmingweb.service.DataService;
import pkuss.programmingweb.service.DataServiceImpl;
import pkuss.programmingweb.service.ReadCSV;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@lombok.Data
public class Data {
    InvertedIndex ii;
    Map<String, API> APImap = new HashMap<>();
    Map<String, Mashup> Mashupmap = new HashMap<>();
    private DataService dataService = new DataServiceImpl();

    public Data(InvertedIndex ii) throws IOException {
        this.ii = ii;
        initData();
    }

    private void initData() throws IOException {
        // load active_apis_data.txt to APImap
        String activeApiDataPath = "static" + File.separator + "active_apis_data.txt";
        APImap.putAll(dataService.loadAPIFromJson(activeApiDataPath, true));

        // load deadpool_apis_data.txt to APImap
        String deadpoolApiDataPath = "static" + File.separator + "deadpool_apis_data.txt";
        APImap.putAll(dataService.loadAPIFromJson(deadpoolApiDataPath, false));

        // load active_mashups_data.txt to Mashupmap
        String activeMashupsDataPath = "static" + File.separator + "active_mashups_data.txt";
        Mashupmap.putAll(dataService.loadMashupFromJson(activeMashupsDataPath, true));

        // load deadpool_mashups_data.txt to Mashupmap
        String deadpoolMashupsDataPath = "static" + File.separator + "deadpool_mashups_data.txt";
        Mashupmap.putAll(dataService.loadMashupFromJson(deadpoolMashupsDataPath, false));

        // load CSVs to APImap & Mashupmap
        InputStream apiFile = new ClassPathResource("static/api_nodes_estimator.csv").getInputStream();
        ReadCSV.readContent(apiFile, APImap, API.class);
        InputStream mashupFile = new ClassPathResource("static/mashup_nodes_estimator.csv").getInputStream();
        ReadCSV.readContent(mashupFile, Mashupmap, Mashup.class);
        InputStream MAPairFile = new ClassPathResource("static/m-a_edges.csv").getInputStream();
        ReadCSV.readM_a(MAPairFile, Mashupmap, APImap);

        // create inverted index
        for (API api : APImap.values()) {
            // lowercase all tags & add API tags to inverted index
            List<String> apiTags = api.getTags();
            for (int i = 0; i < apiTags.size(); i++) {
                String lower = apiTags.get(i).toLowerCase();
                apiTags.set(i, lower);
                ii.setMap(lower, api, type.API_TAGS);
            }

            // lowercase API name & add API name to inverted index
            String apiName = api.getTitle().toLowerCase();
            for (String keyword : apiName.split(" "))
                ii.setMap(keyword, api, type.API_NAME);
        }

        // add apis related with Mashup to mashupTitle-apis inverted index
        for (Mashup mashup : Mashupmap.values()) {
//            List<String> mashupCategory = mashup.getCategories();
//
//            for (int i = 0; i < mashupCategory.size(); i++) {
//                String lowerCategory = mashupCategory.get(i).toLowerCase();
//                mashupCategory.set(i, lowerCategory);
//
//                List<API> mashupAPI=mashup.getRelatedApis();
//                for (API api : mashupAPI) {
//                    ii.setMap(lowerCategory, api, type.MASHUP_CATEGORY);
//                }
//            }
            String mashupName = mashup.getTitle();
            List<API> mashupAPI = mashup.getRelatedApis();
            for (API api : mashupAPI) {
                ii.setMap(mashupName.toLowerCase(), api, type.MASHUP_NAME);
            }
        }
//
//        System.out.println("ok");
    }
}
