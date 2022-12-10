package pkuss.programmingweb.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Repository
public class InvertedIndex {

    Map<String, Set<API>> apiNameInvertindex = new HashMap<>();
    Map<String, Set<API>> apiCategoryInvertindex = new HashMap<>();
    Map<String, Set<API>> mashupNameInvertindex = new HashMap<>();
    Map<String, Set<API>> mashupCategoryInvertindex = new HashMap<>();

    public void setMap(String keyword, API api, type t) {
        Map<String, Set<API>> invertindex = getInvertindex(t);

        invertindex.putIfAbsent(keyword, new HashSet<>());
        invertindex.get(keyword).add(api);
    }

    public Map<String, Set<API>> getInvertindex(type t) {
        switch (t) {
            case API_NAME:
                return apiNameInvertindex;
            case API_CATEGORY:
                return apiCategoryInvertindex;
            case MASHUP_NAME:
                return mashupNameInvertindex;
            case MASHUP_CATEGORY:
                return mashupCategoryInvertindex;
            default:
                return new HashMap<>();
        }
    }

    public Set<API> search(String keyword, type t) {
        Map<String, Set<API>> invertindex = getInvertindex(t);
        return invertindex.getOrDefault(keyword, new HashSet<>());
    }
}
