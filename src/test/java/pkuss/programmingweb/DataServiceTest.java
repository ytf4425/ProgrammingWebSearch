package pkuss.programmingweb;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pkuss.programmingweb.service.DataService;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * Author: SongXJ
 * Date: 2022/12/8 22:19
 * Description: 从 Json 文件中加载 active_mashup 的测试
 */
@SpringBootTest
public class DataServiceTest {
    @Resource
    private DataService dataService;

    @Test
    void contextLoads() throws IOException {
        System.out.println(dataService.loadMashupFromJson());
    }
}
