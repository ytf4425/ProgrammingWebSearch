package pkuss.programmingweb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ProgrammableWeb {
    protected String name; //mashup的名字 mashup:xxx
    protected String url;//mashup对应url https://xxx
    protected String category;//mashup对应的类别
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
        setUrl(line[1]);
        setName(line[2]);
        setSt(line[3]);
        setEt(line[4]);
        setOet(line[5]);
        setCategory(line[6]);
        setOac(line[7]);
        setAc(line[8]);
    }
}
