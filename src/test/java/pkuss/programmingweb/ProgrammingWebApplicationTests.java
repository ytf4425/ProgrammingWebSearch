package pkuss.programmingweb;

import org.hamcrest.Condition;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pkuss.programmingweb.entity.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@SpringBootTest
class ProgrammingWebApplicationTests {

    //输出邻接表
    void testGraph(Graph g)
    {
        System.out.println(g);
    }

    //重要方法，本方法将进行测试命中率
    //参数说明 maxcategory 限制关键词个数，由于经测试，关键词大于16很可能会爆内存，请勿大于18，不然真的要炸的
    // mincategory 最少的关键词个数
    // min和max 取闭区间
    // isweighted 是否使用有权图
    void testaccuracy(Graph g,InvertedIndex ii,int mincategory,int maxcategory,boolean isweighted)
    {
        //这里千万注意了，千万不能用type.Mashup_category
        //因为我们的图是根据每个mashup建立的，但是如果选择mashup的category，这些mashup有可能是会重复，或者不连通的，这一定是错误的
        Map<String, Set<API>> apisfromMashup = ii.getInvertindex(type.MASHUP_NAME);
        //cnt是计数器
        int cnt = 0;
        //命中次数
        int hit=0;
        // 广义命中率  返回API的平均命中率
        double EveryAPIhitting = 0;

        for(Map.Entry<String,Set<API>> entry:apisfromMashup.entrySet())
        {
            Set<String> stringset = new HashSet<>();
            Set<Integer> ApiIndexes = new HashSet<>();
            Set<API> set = entry.getValue();
            for(API api:set)
            {
                stringset.add(api.getCategory().toLowerCase());
                ApiIndexes.add(api.getIndexInGraph());
            }

            if(stringset.size() < mincategory)
            {
                 continue;
            }
            if(stringset.size() > maxcategory)
            {
                 continue;
            }





            String[] strings = stringset.toArray(new String[stringset.size()]);
            Set<API> resultset = g.searchbyStenierTree(strings,ii,true);
            Set<Integer> resultIntset = new HashSet<>(g.getResultlist());
            //unionset 是 mashup的apiset 和 返回结果集的apiset的交集
            // 其实我用的是graph里面的那个resultlist,那个东西里面存着斯坦纳树所有节点编号
            Set<Integer> unionset = new HashSet<>();
            unionset.addAll(resultIntset);
            unionset.retainAll(ApiIndexes);
            // 想要输出里面的结果的话可以去掉注释，我将依次输出api的名字和编号，结果集中api的编号
            /*
            System.out.println(cnt);
            System.out.print("API:");
            System.out.println(ApiIndexes);
            System.out.print("stringset:");
            System.out.println(stringset);
            System.out.println(resultIntset);
            */
             //正常来说，下面这行不会触发，至少我测试的时候没触发，但是为了安全性还是加了
            if(resultset == null)
                continue;
            cnt++;
            // 广义命中率 交集元素个数/mashup中的api个数
            double accuracy = 1.0*unionset.size()/ApiIndexes.size();
            //下面的注释去掉就可以看结果，看一下是哪些api被用到了，这里其实挺重要的
            /*
            for(API apii:resultset)
            {
                System.out.print(apii.getCategory().toLowerCase()+",");
            }

            System.out.println("");
            //System.out.println(resultset);
            System.out.print("union:");
            System.out.println(unionset);

             */
            if(unionset.size() == ApiIndexes.size())
            {
                hit++;
                //System.out.println("完全命中");
            }

            EveryAPIhitting += accuracy;
        }
        System.out.println("广义命中率：");
        System.out.println(1.0*EveryAPIhitting/cnt);
        System.out.println("完全命中的次数：");
        System.out.println(hit);
        System.out.println("遍历的mashup总数");
        System.out.println(cnt);
        System.out.println("命中率（命中次数/mashup总数）：");
        System.out.println(1.0*hit/cnt);
    }


    @Test
    void contextLoads() throws IOException {
        InvertedIndex ii = new InvertedIndex();
        Data data = new Data(ii);
        //建好的图
        Graph g = new Graph(data);
        //测试查询命中率
        testaccuracy(g,ii,2,2,true);


    }
}
