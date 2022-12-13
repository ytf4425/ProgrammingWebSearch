package pkuss.programmingweb.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Author: SongXJ
 * Date: 2022/12/8 22:28
 * Description: 从 static/raw/api_mashup/active_mashups_data.txt 文件中读取的 Mashup 类中的 Api
 */

@Getter
@Setter
public class ActiveApi {
    String title;
    List<String> tags;
    String description;
    String url;
    List<version> versions;

    @Override
    public String toString() {
        return "ActiveApi{" +
                "title='" + title + '\'' +
                ", tags=" + tags +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", versions=" + versions +
                '}';
    }

    @Getter
    @Setter
    public static class version {
        String version_title;
        String style;
        String version;
        String status;
        String submit_date;
    }
}
