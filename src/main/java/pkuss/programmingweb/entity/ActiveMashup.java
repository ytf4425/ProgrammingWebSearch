package pkuss.programmingweb.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Author: SongXJ
 * Date: 2022/12/8 22:10
 * Description: 从 static/raw/api_mashup/active_mashups_data.txt 文件中读取的 Mashup 类
 */

@Getter
@Setter
public class ActiveMashup {
    String title;
    List<String> tags;
    String description;
    List<ActiveApi> related_apis;
    List<String> categories;
    String url;
    String mashup_type;
    String date;

    @Override
    public String toString() {
        return "ActiveMashup{" +
                "title='" + title + '\'' +
                ", tags=" + tags +
                ", description='" + description + '\'' +
                ", related_apis=" + related_apis +
                ", categories=" + categories +
                ", url='" + url + '\'' +
                ", mashup_type='" + mashup_type + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
