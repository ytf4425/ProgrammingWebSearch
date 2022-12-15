package pkuss.programmingweb.service;


import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import pkuss.programmingweb.entity.API;
import pkuss.programmingweb.entity.Mashup;
import pkuss.programmingweb.entity.ProgrammableWeb;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: SongXJ
 * Date: 2022/12/8 22:10
 * Description: 读取 JSON 数据服务的接口实现
 */

@Service
public class DataServiceImpl implements DataService {
    private <T extends ProgrammableWeb> List<T> readJsonFile(String dataPath, Class<T> dataClass) throws IOException {
        File file = ResourceUtils.getFile("classpath:" + dataPath);
        String json = FileUtils.readFileToString(file, "UTF-8");
        return JSON.parseArray(json, dataClass);
    }

    private <T extends ProgrammableWeb> Map<String, T> loadFromJson(String dataPath, Boolean active, Class<T> iClass) throws IOException {
        Map<String, T> result = new HashMap<>();
        List<T> items = readJsonFile(dataPath, iClass);
        for (T item : items) {
            if (item == null) continue;
            item.setActive(active);                 // set active state
            item.setUrl(item.getUrl().trim());      // remove "\n" at the end of URL
            result.put(item.getIndex(), item);      // add to Map<index, item>
        }

        return result;
    }

    public Map<String, API> loadAPIFromJson(String dataPath, Boolean active) throws IOException {
        return loadFromJson(dataPath, active, API.class);
    }

    public Map<String, Mashup> loadMashupFromJson(String dataPath, Boolean active) throws IOException {
        return loadFromJson(dataPath, active, Mashup.class);
    }
}
