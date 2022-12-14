package pkuss.programmingweb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class ProgrammableWeb {
    String title;
    List<String> tags;  // tags in Mashup seem to be null
    String description;
    String url;

    Boolean active = true;

    // For Deadpool APIs & Mashups
    List<String> changelogs;
    String deprecated_date_estimated;

//    @Override
//    public String toString() {
//        return "ActiveApi{" +
//                "title='" + title + '\'' +
//                ", tags=" + tags +
//                ", description='" + description + '\'' +
//                ", url='" + url + '\'' +
//                ", versions=" + versions +
//                '}';
//    }


//    protected String name;
//    protected String url;
//    protected String category;
    protected String st;  // Submit date
    protected String et;  // Corrected dead date
    @JsonIgnore
    protected String oet; // Dead date provided in PW
    protected String ac;  // Accessibility provided in PW
    @JsonIgnore
    protected String oac; // Corrected accessibility

    public ProgrammableWeb(String[] line) {
        set(line);
    }

    public String getIndex() {
        return null;
    }

    public void set(String[] line) {
//        setUrl(line[1]);
//        setName(line[2]);
        setSt(line[3]);
        setEt(line[4]);
        setOet(line[5]);
//        setCategory(line[6]);
        setOac(line[7]);
        setAc(line[8]);
    }
}
