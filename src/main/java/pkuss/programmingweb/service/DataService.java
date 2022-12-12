package pkuss.programmingweb.service;

import pkuss.programmingweb.entity.ActiveApi;
import pkuss.programmingweb.entity.ActiveMashup;

import java.io.IOException;
import java.util.List;

/**
 * Author: SongXJ
 * Date: 2022/12/8 22:10
 * Description: 读取数据服务的接口
 */

public interface DataService {
    // 从JSON中加载Mashup数据
    List<ActiveMashup> loadMashupFromJson() throws IOException;
    List<ActiveApi> loadAPIFromJson() throws IOException;
}
