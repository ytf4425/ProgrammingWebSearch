package pkuss.programmingweb.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pkuss.programmingweb.entity.API;
import pkuss.programmingweb.entity.Data;
import pkuss.programmingweb.entity.Graph;
import pkuss.programmingweb.entity.InvertedIndex;
import sun.font.TrueTypeFont;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class main {

    InvertedIndex ii = new InvertedIndex();
    Data data = new Data(ii);
    Graph g = new Graph(data);

    public main() throws IOException {
    }


    @GetMapping("/api/")
    public Set<API> search(
            @RequestParam(value = "category", required = false) List<String> categorys,
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        Set<API>  res = null;

        if (categorys != null) {
            String[] c = categorys.toArray(new String[categorys.size()]);
            for (int i = 0; i < c.length; i++) {
                c[i] = c[i].toLowerCase();
            }
            res = g.searchbyStenierTree(c, ii, true);
        } else if (keyword != null) {

            res = new HashSet<API>();
        }

        return res;
    }


}


