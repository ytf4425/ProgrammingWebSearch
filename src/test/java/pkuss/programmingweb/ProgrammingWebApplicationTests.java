package pkuss.programmingweb;

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
    //参数说明 maxtags 限制关键词个数，由于经测试，关键词大于16很可能会爆内存，请勿大于18，不然真的要炸的
    // mintags 最少的关键词个数
    // min和max 取闭区间
    // isweighted 是否使用有权图
    void testaccuracyUsingTags(Graph g,InvertedIndex ii,int mintags,int maxtags,boolean isweighted)
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
                stringset.addAll(api.getLowercaseTags());
                ApiIndexes.add(api.getIndexInGraph());
            }
            if(stringset.size() < mintags)
            {
                continue;
            }
            if(stringset.size() > maxtags)
            {
                continue;
            }
            System.out.println("----------------------------------------------------------");
            String[] strings = stringset.toArray(new String[stringset.size()]);
            Set<API> resultset = g.searchbyStenierTree(strings,ii,true);
            Set<Integer> resultIntset = new HashSet<>(g.getResultlist());
            //unionset 是 mashup的apiset 和 返回结果集的apiset的交集
            // 其实我用的是graph里面的那个resultlist,那个东西里面存着斯坦纳树所有节点编号
            Set<Integer> unionset = new HashSet<>();
            unionset.addAll(resultIntset);
            unionset.retainAll(ApiIndexes);
            // 想要输出里面的结果的话可以去掉注释，我将依次输出api的名字和编号，结果集中api的编号
            System.out.print("这是第");
            System.out.print(cnt);
            System.out.print("次实验\n这个Mashup中的API编号分别是:\n");
            System.out.println(ApiIndexes);
            System.out.println("这个Mashup中所有API包含的所有Tags是:");
            System.out.println(stringset);
            System.out.println("已经构造好了的斯坦纳树，这棵树的结点的API编号是");
            System.out.println(resultIntset);

            //正常来说，下面这行不会触发，至少我测试的时候没触发，但是为了安全性还是加了
            if(resultset == null)
                continue;
            cnt++;
            // 广义命中率 交集元素个数/mashup中的api个数
            double accuracy = 1.0*unionset.size()/ApiIndexes.size();
            //下面的注释去掉就可以看结果，看一下是哪些api被用到了，这里其实挺重要的
            Set<String> resultStringSet = new HashSet<>();
            for(API apii:resultset)
            {
                resultStringSet.addAll(apii.getTags());
            }
            System.out.println("斯坦纳树中包含的所有结点的tags集合是");
            System.out.println(resultStringSet);
            System.out.println("斯坦纳树结果集 和 Mashup的输入 的 交集，也就是说，被完全命中的API编号:");
            System.out.println(unionset);
            System.out.println("最小斯坦纳树的权值和:");
            System.out.println(g.getMinDpResult());


            if(unionset.size() == ApiIndexes.size())
            {
                hit++;
                System.out.println("本次实验完全命中");
            }

            EveryAPIhitting += accuracy;
            System.out.println("----------------------------------------------------------");
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

    // 这个方法将会测试 ：一个mashup仅保留首尾两个API的tags，隐匿其他关键词的查询命中率
    // 它将会对比 所有关键词的情况
    void testaccuracyFirstApiandLastApi(Graph g,InvertedIndex ii,int mintags,int maxtags,boolean isweighted)
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
        // 计数，计算有多少个使用首尾tags的结果集 和 使用全部tags获得的结果集 是一样的
        int sameapis = 0;

        for(Map.Entry<String,Set<API>> entry:apisfromMashup.entrySet())
        {

            Set<String> stringset = new HashSet<>();
            Set<Integer> ApiIndexes = new HashSet<>();
            Set<API> set = entry.getValue();
            Set<String> stringsetfirstandlast = new HashSet<>();
            Set<Integer> ApiIndexesfirstandlast = new HashSet<>();
            int cnt_set = 0;
            for(API api:set)
            {
                cnt_set++;
                if(cnt_set == 1)
                {
                    stringsetfirstandlast.addAll(api.getLowercaseTags());
                    ApiIndexesfirstandlast.add(api.getIndexInGraph());
                }
                if(cnt_set == set.size())
                {
                    stringsetfirstandlast.addAll(api.getLowercaseTags());
                    ApiIndexesfirstandlast.add(api.getIndexInGraph());
                }
                stringset.addAll(api.getLowercaseTags());
                ApiIndexes.add(api.getIndexInGraph());
            }
            if(stringset.size() < mintags)
            {
                continue;
            }
            if(stringset.size() > maxtags)
            {
                continue;
            }
            System.out.println("----------------------------------------------------------");
            String[] strings = stringset.toArray(new String[stringset.size()]);
            String[] stringsfirstandlast = stringsetfirstandlast.toArray(new String[stringsetfirstandlast.size()]);
            Set<API> resultset = g.searchbyStenierTree(strings,ii,true);
            Set<Integer> resultIntset = new HashSet<>(g.getResultlist());
            //unionset 是 mashup的apiset 和 返回结果集的apiset的交集
            // 其实我用的是graph里面的那个resultlist,那个东西里面存着斯坦纳树所有节点编号
            Set<Integer> unionset = new HashSet<>();
            unionset.addAll(resultIntset);
            unionset.retainAll(ApiIndexes);
            // 想要输出里面的结果的话可以去掉注释，我将依次输出api的名字和编号，结果集中api的编号
            System.out.print("这是第");
            System.out.print(cnt);
            System.out.print("次实验\n这个Mashup中的API编号分别是:\n");
            System.out.println(ApiIndexes);
            System.out.println("这个Mashup中所有API包含的所有Tags是:");
            System.out.println(stringset);
            System.out.println("使用所有tags，已经构造好了的斯坦纳树，这棵树的结点的API编号是");
            System.out.println(resultIntset);
            //正常来说，下面这行不会触发，至少我测试的时候没触发，但是为了安全性还是加了
            if(resultset == null)
                continue;

            // 广义命中率 交集元素个数/mashup中的api个数
            //double accuracy = 1.0*unionset.size()/ApiIndexes.size();
            //下面的注释去掉就可以看结果，看一下是哪些api被用到了，这里其实挺重要的
            Set<String> resultStringSet = new HashSet<>();
            for(API apii:resultset)
            {
                resultStringSet.addAll(apii.getTags());
            }
            /*
            System.out.println("斯坦纳树中包含的所有结点的tags集合是");
            System.out.println(resultStringSet);
            System.out.println("斯坦纳树结果集 和 Mashup的输入 的 交集，也就是说，被完全命中的API编号:");
            System.out.println(unionset);
            System.out.println("使用所有tags的时候，最小斯坦纳树的权值和:");
            System.out.println(g.getMinDpResult());
            */
            if(unionset.size() == ApiIndexes.size())
            {
                /*
                System.out.println("使用所有tags的时候，本次实验完全命中");
                 */
            }

            Set<API> resultsetfirstandlast = g.searchbyStenierTree(stringsfirstandlast,ii,true);
            Set<Integer> resultIntsetfirstandlast = new HashSet<>(g.getResultlist());
            Set<Integer> unionsetfirstandlast = new HashSet<>();
            unionsetfirstandlast.addAll(resultIntsetfirstandlast);
            unionsetfirstandlast.retainAll(ApiIndexes);
            /*
            System.out.print("第一个元素和最后一个元素的API编号分别是:\n");
            System.out.println(ApiIndexesfirstandlast);
            System.out.println("第一个元素和最后一个元素的Category是:");
            System.out.println(stringsetfirstandlast);
            */
            System.out.println("如果使用首尾tags，构建的斯坦纳树的结点的API编号是");
            System.out.println(resultIntsetfirstandlast);
            //正常来说，下面这行不会触发，至少我测试的时候没触发，但是为了安全性还是加了
            if(resultsetfirstandlast == null)
                continue;
            cnt++;
            // 广义命中率 交集元素个数/mashup中的api个数
            double accuracy = 1.0*unionsetfirstandlast.size()/ApiIndexes.size();
            //下面的注释去掉就可以看结果，看一下是哪些api被用到了，这里其实挺重要的
            Set<String> resultStringSetfirstandlast = new HashSet<>();
            for(API apii:resultsetfirstandlast)
            {
                resultStringSetfirstandlast.addAll(apii.getTags());
            }
            /*
            System.out.println("使用首尾tags，斯坦纳树中包含的所有结点的tags集合是");
            System.out.println(resultStringSetfirstandlast);
            System.out.println("使用首尾tags，被完全命中的API编号:");
            System.out.println(unionsetfirstandlast);
            System.out.println("使用首尾tags，最小斯坦纳树的权值和:");
            System.out.println(g.getMinDpResult());
             */
            if(unionsetfirstandlast.size() == ApiIndexes.size())
            {
                hit++;
                System.out.println("使用第一个，最后一个tags的时候，本次实验完全命中");
            }
            //下面这几行代码用来比较使用首尾tags的结果集 和 使用全部tags的结果集之间的区别
            Set<Integer> resultunionset = new HashSet<>();
            resultunionset.addAll(resultIntset);
            resultunionset.retainAll(resultIntsetfirstandlast);
            System.out.println("如果使用首尾tags，有"+resultunionset.size()+"个api和原先的结果集相同");
            if(resultunionset.size() == resultIntset.size())
                sameapis++;
            // 代码块结束
            EveryAPIhitting += accuracy;
            System.out.println("----------------------------------------------------------");
        }
        System.out.println("使用首尾tags的广义命中率：");
        System.out.println(1.0*EveryAPIhitting/cnt);
        System.out.println("完全命中的次数：");
        System.out.println(hit);
        System.out.println("遍历的mashup总数");
        System.out.println(cnt);
        System.out.println("使用首尾tags的命中率（命中次数/mashup总数）：");
        System.out.println(1.0*hit/cnt);
        System.out.println("使用首尾tags 和 使用全部tags 获得的结果集相同的情况一共有"+sameapis+"个");
        System.out.println("结果集相同的概率是"+1.0*sameapis/cnt);
    }

    void testaccuracyFirstTagandLastTag(Graph g,InvertedIndex ii,int mintags,int maxtags,boolean isweighted)
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
        // 计数，计算有多少个使用首尾tags的结果集 和 使用全部tags获得的结果集 是一样的
        int sameapis = 0;

        for(Map.Entry<String,Set<API>> entry:apisfromMashup.entrySet())
        {

            Set<String> stringset = new HashSet<>();
            Set<Integer> ApiIndexes = new HashSet<>();
            Set<API> set = entry.getValue();
            Set<String> stringsetfirstandlast = new HashSet<>();
            Set<Integer> ApiIndexesfirstandlast = new HashSet<>();
            int cnt_set = 0;
            for(API api:set)
            {
                cnt_set++;
                if(cnt_set == 1)
                {
                    String c;
                    if(api.getLowercaseTags().size()==0)
                        c="None";
                    else
                        c=api.getLowercaseTags().get(0);
                    stringsetfirstandlast.add(c);
                    ApiIndexesfirstandlast.add(api.getIndexInGraph());
                }
                if(cnt_set == set.size())
                {
                    String c;
                    if(api.getLowercaseTags().size()==0)
                        c="None";
                    else
                        c=api.getLowercaseTags().get(0);
                    stringsetfirstandlast.add(c);
                    ApiIndexesfirstandlast.add(api.getIndexInGraph());
                }
                stringset.addAll(api.getLowercaseTags());
                ApiIndexes.add(api.getIndexInGraph());
            }
            if(stringset.size() < mintags)
            {
                continue;
            }
            if(stringset.size() > maxtags)
            {
                continue;
            }
            System.out.println("----------------------------------------------------------");
            String[] strings = stringset.toArray(new String[stringset.size()]);
            String[] stringsfirstandlast = stringsetfirstandlast.toArray(new String[stringsetfirstandlast.size()]);
            Set<API> resultset = g.searchbyStenierTree(strings,ii,true);
            Set<Integer> resultIntset = new HashSet<>(g.getResultlist());
            //unionset 是 mashup的apiset 和 返回结果集的apiset的交集
            // 其实我用的是graph里面的那个resultlist,那个东西里面存着斯坦纳树所有节点编号
            Set<Integer> unionset = new HashSet<>();
            unionset.addAll(resultIntset);
            unionset.retainAll(ApiIndexes);
            // 想要输出里面的结果的话可以去掉注释，我将依次输出api的名字和编号，结果集中api的编号
            System.out.print("这是第");
            System.out.print(cnt);
            System.out.print("次实验\n这个Mashup中的API编号分别是:\n");
            System.out.println(ApiIndexes);
            System.out.println("这个Mashup中所有API包含的所有Tags是:");
            System.out.println(stringset);
            System.out.println("使用所有tags，已经构造好了的斯坦纳树，这棵树的结点的API编号是");
            System.out.println(resultIntset);
            //正常来说，下面这行不会触发，至少我测试的时候没触发，但是为了安全性还是加了
            if(resultset == null)
                continue;

            // 广义命中率 交集元素个数/mashup中的api个数
            //double accuracy = 1.0*unionset.size()/ApiIndexes.size();
            //下面的注释去掉就可以看结果，看一下是哪些api被用到了，这里其实挺重要的
            Set<String> resultStringSet = new HashSet<>();
            for(API apii:resultset)
            {
                resultStringSet.addAll(apii.getTags());
            }
            /*
            System.out.println("斯坦纳树中包含的所有结点的tags集合是");
            System.out.println(resultStringSet);
            System.out.println("斯坦纳树结果集 和 Mashup的输入 的 交集，也就是说，被完全命中的API编号:");
            System.out.println(unionset);
            System.out.println("使用所有tags的时候，最小斯坦纳树的权值和:");
            System.out.println(g.getMinDpResult());
            */
            if(unionset.size() == ApiIndexes.size())
            {
                /*
                System.out.println("使用所有tags的时候，本次实验完全命中");
                 */
            }

            Set<API> resultsetfirstandlast = g.searchbyStenierTree(stringsfirstandlast,ii,true);
            Set<Integer> resultIntsetfirstandlast = new HashSet<>(g.getResultlist());
            Set<Integer> unionsetfirstandlast = new HashSet<>();
            unionsetfirstandlast.addAll(resultIntsetfirstandlast);
            unionsetfirstandlast.retainAll(ApiIndexes);
            /*
            System.out.print("第一个元素和最后一个元素的API编号分别是:\n");
            System.out.println(ApiIndexesfirstandlast);
            System.out.println("第一个元素和最后一个元素的Category是:");
            System.out.println(stringsetfirstandlast);
            */
            System.out.println("如果使用首尾API的tags，构建的斯坦纳树的结点的API编号是");
            System.out.println(resultIntsetfirstandlast);
            //正常来说，下面这行不会触发，至少我测试的时候没触发，但是为了安全性还是加了
            if(resultsetfirstandlast == null)
                continue;
            cnt++;
            // 广义命中率 交集元素个数/mashup中的api个数
            double accuracy = 1.0*unionsetfirstandlast.size()/ApiIndexes.size();
            //下面的注释去掉就可以看结果，看一下是哪些api被用到了，这里其实挺重要的
            Set<String> resultStringSetfirstandlast = new HashSet<>();
            for(API apii:resultsetfirstandlast)
            {
                resultStringSetfirstandlast.addAll(apii.getTags());
            }
            /*
            System.out.println("使用首尾tags，斯坦纳树中包含的所有结点的tags集合是");
            System.out.println(resultStringSetfirstandlast);
            System.out.println("使用首尾tags，被完全命中的API编号:");
            System.out.println(unionsetfirstandlast);
            System.out.println("使用首尾tags，最小斯坦纳树的权值和:");
            System.out.println(g.getMinDpResult());
             */
            if(unionsetfirstandlast.size() == ApiIndexes.size())
            {
                hit++;
                System.out.println("使用第一个，最后一个API的tags的时候，本次实验完全命中");
            }
            //下面这几行代码用来比较使用首尾tags的结果集 和 使用全部tags的结果集之间的区别
            Set<Integer> resultunionset = new HashSet<>();
            resultunionset.addAll(resultIntset);
            resultunionset.retainAll(resultIntsetfirstandlast);
            System.out.println("如果使用首尾API的tags，有"+resultunionset.size()+"个api和原先的结果集相同");
            if(resultunionset.size() == resultIntset.size())
                sameapis++;
            // 代码块结束
            EveryAPIhitting += accuracy;
            System.out.println("----------------------------------------------------------");
        }
        System.out.println("使用首尾API的tags的广义命中率：");
        System.out.println(1.0*EveryAPIhitting/cnt);
        System.out.println("完全命中的次数：");
        System.out.println(hit);
        System.out.println("遍历的mashup总数");
        System.out.println(cnt);
        System.out.println("使用首尾API的tags的命中率（命中次数/mashup总数）：");
        System.out.println(1.0*hit/cnt);
        System.out.println("使用首尾API的tags 和 使用全部tags 获得的结果集相同的情况一共有"+sameapis+"个");
        System.out.println("结果集相同的概率是"+1.0*sameapis/cnt);
    }



    @Test
    void contextLoads() throws IOException {
        InvertedIndex ii = new InvertedIndex();
        Data data = new Data(ii);
        //建好的图
        Graph g = new Graph(data);
        //测试查询命中率
//        testaccuracyFirstTagandLastTag(g,ii,3,3,true);
//        testaccuracyUsingTags(g,ii,3,3,true);
        testaccuracyFirstApiandLastApi(g,ii,3,3,true);
        /*
        String[] tags = {"analytics", "images", "meme", "identity", "mobile", "recognition", "media", "storage", "big data", "machine learning", "content", "applications"};
        Set<API> res = g.searchbyStenierTree(tags,ii,true);
        System.out.println(res);

         */

    }
}
