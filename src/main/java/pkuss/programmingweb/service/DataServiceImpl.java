package pkuss.programmingweb.service;


import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import pkuss.programmingweb.entity.ActiveMashup;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Author: SongXJ
 * Date: 2022/12/8 22:10
 * Description: 读取数据服务的接口实现
 */

@Service
public class DataServiceImpl implements DataService{
    @Override
    // 从JSON中加载Mashup数据
    public List<ActiveMashup> loadMashupFromJson() throws IOException {
        File file = ResourceUtils.getFile("classpath:static/raw/api_mashup/active_mashups_data.txt");
        String json = FileUtils.readFileToString(file, "UTF-8");
        return JSON.parseArray(json, ActiveMashup.class);
    }

}
