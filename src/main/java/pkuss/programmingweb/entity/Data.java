package pkuss.programmingweb.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.metrics.StartupStep;
import pkuss.programmingweb.dao.ReadCSV;
import pkuss.programmingweb.service.DataService;
import pkuss.programmingweb.service.DataServiceImpl;

import javax.annotation.Resource;
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
    private InputStream Api_file_path = new ClassPathResource("static/api_nodes_estimator.csv").getInputStream();
    private InputStream Mashup_file_path = new ClassPathResource("static/mashup_nodes_estimator.csv").getInputStream();
    private InputStream M_A_file_path = new ClassPathResource("static/m-a_edges.csv").getInputStream();

    public Data(InvertedIndex ii) throws IOException {
        this.ii = ii;
        initData();
    }

    private void initData() throws IOException {
        ReadCSV.readContent(getApi_file_path(), getAPImap(), API.class);
        ReadCSV.readContent(getMashup_file_path(), getMashupmap(), Mashup.class);
        ReadCSV.readM_a(getM_A_file_path(), getMashupmap(), getAPImap());
        List<ActiveApi> RawAPIlist = dataService.loadAPIFromJson();
        for (API api : getAPImap().values()) {
            api.setCategory(api.getCategory().toLowerCase());
            // add API basic information to inverted index
            String apiCategory = api.getCategory();
            ii.setMap(apiCategory.toLowerCase(), api, type.API_CATEGORY);

            String apiName = api.getName();
            ii.setMap(apiName.toLowerCase(), api, type.API_NAME);

            // add related Mashup App information to inverted index
            for (Mashup mashup : api.getMashup()) {
                String mashupCategory = mashup.getCategory();
                ii.setMap(mashupCategory.toLowerCase(), api, type.MASHUP_CATEGORY);
                String mashupName = mashup.getName();
                //System.out.println(mashupName);
                ii.setMap(mashupName.toLowerCase(), api, type.MASHUP_NAME);
            }
        }
        for(ActiveApi api:RawAPIlist)
        {
            if(api == null)
                continue;
            String url = api.getUrl().trim();
            API Api_in_map = null;
            if(url!=null) Api_in_map = APImap.get(url);
            List<String> tags = api.getTags();
            if(Api_in_map != null)
            {
                for(String tag:tags)
                {
                    Api_in_map.getTags().add(tag);
                }
                for(String tag:tags)
                {
                    ii.setMap(tag.toLowerCase(),Api_in_map,type.TAGS_NAME);
                }
            }
        }
        for(API api:APImap.values())
        {
            if(api.getTags().isEmpty())
            {
                api.getTags().add(api.getCategory());
                ii.setMap(api.getCategory(),api,type.TAGS_NAME);
            }
        }
    }
}
