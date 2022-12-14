package pkuss.programmingweb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Mashup extends ProgrammableWeb {
    @JsonIgnore
    private List<API> relatedApis = new ArrayList<>(); // 此 Mashup 应用所用的 API

    List<String> categories;
    String mashup_type;
    String date;
    String page_url;    // For Deadpoll Mashups

    public Mashup(String[] line) {
        super(line);
    }

    @JsonIgnore
    public String getIndex() {
        return getTitle();
    }

    @Override
    public String toString() {
        StringBuilder returnInfo = new StringBuilder(title);
        returnInfo.append("\n\t\t\ttags: " + tags);
        returnInfo.append("\n\t\t\turl: " + url);
        returnInfo.append("\n\t\t\tSubmit date: " + st);
        returnInfo.append("\n\t\t\tCorrected dead date: " + et);
        returnInfo.append("\n\t\t\tDead date provided in PW: " + oet);
        returnInfo.append("\n\t\t\tCorrected accessibility: " + oac);
        returnInfo.append("\n\t\t\tAccessibility provided in PW: " + ac);
        return returnInfo.toString();
    }
}
