package pkuss.programmingweb.service;


import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import pkuss.programmingweb.entity.ActiveApi;
import pkuss.programmingweb.entity.ActiveMashup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: SongXJ
 * Date: 2022/12/8 22:10
 * Description: 读取数据服务的接口实现
 */

public class DataServiceImpl implements DataService {
    @Override
    // 从JSON中加载Mashup数据
    public List<ActiveMashup> loadMashupFromJson() throws IOException {
        String sourcePath = "static" + File.separator + "active_mashups_data.txt";
        File file = ResourceUtils.getFile("classpath:" + sourcePath);
        String json = FileUtils.readFileToString(file, "UTF-8");
        return JSON.parseArray(json, ActiveMashup.class);
    }

    public List<ActiveApi> loadAPIFromJson() throws IOException {
        String sourcePath = "static" +File.separator + "active_apis_data.txt";
        File file = ResourceUtils.getFile("classpath:" + sourcePath);
        String json = FileUtils.readFileToString(file, "UTF-8");
        List<ActiveApi> result = JSON.parseArray(json, ActiveApi.class);
        for(ActiveApi api:result)
        {
            List<String> tmp = new ArrayList<>();
            if(api!=null)
            {
                for(String tag:api.getTags())
                {
                    tmp.add(tag.toLowerCase());
                }
                if(tmp!=null) api.setTags(new ArrayList<String>(tmp));
            }
        }
        return result;
    }
}
