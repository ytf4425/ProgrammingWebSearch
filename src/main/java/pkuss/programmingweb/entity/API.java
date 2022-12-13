package pkuss.programmingweb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: SongXJ
 * Date: 2022/11/18 16:41
 * Description: API实体类
 */

@Getter
@Setter
public class API extends ProgrammableWeb {
    @JsonIgnore
    private int indexInGraph;
    private List<String> tags = new ArrayList<>();
    @JsonIgnore
    private List<Mashup> mashup = new ArrayList<>(); // 采用了此 API 的 Mashup 应用

    public API(String[] line) {
        super(line);
    }

    @JsonIgnore
    public String getIndex() {
        return getUrl();
    }

    @Override
    public String toString() {
        StringBuilder returnInfo = new StringBuilder("API:");
        returnInfo.append("\n\tname: " + name);
        returnInfo.append("\n\tcategory: " + category);
        returnInfo.append("\n\turl: https://www.programmableweb.com" + url);
        returnInfo.append("\n\ttags:"+tags);
        returnInfo.append("\n\tSubmit date: " + st);
        returnInfo.append("\n\tCorrected dead date: " + et);
        returnInfo.append("\n\tDead date provided in PW: " + oet);
        returnInfo.append("\n\tCorrected accessibility: " + oac);
        returnInfo.append("\n\tAccessibility provided in PW: " + ac);
        returnInfo.append("\n\tMashUps:");
        /*
        for (Mashup mashupin : mashup) {
            returnInfo.append("\n\t\t" + mashupin);
        }

         */
        return returnInfo.toString();
    }
}
