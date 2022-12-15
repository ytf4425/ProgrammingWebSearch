package pkuss.programmingweb.service;

import pkuss.programmingweb.entity.API;
import pkuss.programmingweb.entity.Mashup;

import java.io.IOException;
import java.util.Map;

/**
 * Author: SongXJ
 * Date: 2022/12/8 22:10
 * Description: 读取数据服务的接口
 */

public interface DataService {
    // 从JSON中加载Mashup数据
    Map<String, Mashup> loadMashupFromJson(String dataPath, Boolean active) throws IOException;

    Map<String, API> loadAPIFromJson(String dataPath, Boolean active) throws IOException;
}
