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
    private List<API> apis = new ArrayList<>(); // 此 Mashup 应用所用的 API


    public Mashup(String[] line) {
        super(line);
    }

    @JsonIgnore
    public String getIndex() {
        return getName();
    }

    @Override
    public String toString() {
        StringBuilder returnInfo = new StringBuilder(name);
        returnInfo.append("\n\t\t\tcategory: " + category);
        returnInfo.append("\n\t\t\turl: " + url);
        returnInfo.append("\n\t\t\tSubmit date: " + st);
        returnInfo.append("\n\t\t\tCorrected dead date: " + et);
        returnInfo.append("\n\t\t\tDead date provided in PW: " + oet);
        returnInfo.append("\n\t\t\tCorrected accessibility: " + oac);
        returnInfo.append("\n\t\t\tAccessibility provided in PW: " + ac);
        return returnInfo.toString();
    }
}
