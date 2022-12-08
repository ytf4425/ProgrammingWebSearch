package pkuss.programmingweb.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pkuss.programmingweb.entity.API;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class main {
    InvertedIndex invertedIndex = new InvertedIndex();
    Data data = new Data(invertedIndex);
    Graph graph = new Graph(data);

    public main() throws IOException {
    }


    @GetMapping("/api/")
    public Set<Set<API>> search(
            @RequestParam(value = "category", required = false) List<String> categorys,
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        Set<Set<API>> res = null;

        if (categorys != null) {
            String[] c = categorys.toArray(new String[categorys.size()]);
            for (int i = 0; i < c.length; i++) {
                c[i] = c[i].toLowerCase();
            }
            res = graph.search(c, type.API_CATEGORY, invertedIndex);
        } else if (keyword != null) {
            res = new HashSet<>();
            res.add(invertedIndex.search(keyword.toLowerCase(), type.API_NAME));
        }

        return res;
    }
}
