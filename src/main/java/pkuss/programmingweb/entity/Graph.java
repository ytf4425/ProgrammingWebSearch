package pkuss.programmingweb.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Setter
@Getter
public class Graph {
    Data data;
    private API vertices[];   // 点的数组

    //邻接表,经过测试总数量为1236，也就是内存最多是1236x1236xdouble
    //邻接表里也存一个map，这是为了建图的方便，里面那个map的key就是链接的节点，double之后就是 1/权重
    public Map<Integer,Map<Integer,Double>> graphTable;
    // 这个邻接表用来存储边权都是1的邻接表
    public Map<Integer,Set<Integer>> unweightedgraphTable;
    //这个map用于映射真正的index和实际图中的index，因为实际上，能有联通edge的节点数只有1236
    //这是为了之后的dp方便，如果我们开一个20000多的数组将会直接爆炸，所以尽量降低到一个1000多的数组，应该可行
    //IndexMap ：真正的节点编号 to 1 2 3 4 （编号从1开始）
    private Map<Integer,Integer> IndexMap;
    //InvertedIndexMap： 1 2 3 4 to 真正的编号
    private Map<Integer,Integer> InvertedIndexMap;
    //图中的节点数+1，这个用来开数组
    int maxnum;
    //dp数组
    double[][] dp;
    //此数组用于dp初始化，记录所有初始化为0的状态码
    //这是因为一个api有多个tags,那么这些tags如果都出现在关键词里，就需要组合初始状态
    //比如一个api有a，b，c三个tags，但是搜索关键词中出现了a，b，那么a，b，ab三种状态都需要置为1
    //这有一定的时间开销，但是这是没办法的事情，我们发现一个api最多69个tags,很可能会有一定的指数级时间开销
    // 需要近似解决，不能初始化所有状态，因为后面状态压缩dp的事情，所以尽力记录状态即可
    // 不会记录到所有状态，但是必须记录到 ab这种状态（满状态），这样满足题意：找到尽量少的api
    int dpinit[];
    // SPFA算法用的queue
    Queue<Integer> queue;
    // vis数组用于SPFA算法中的判断
    int[] vis;
    // 这个数组用来标记，返回结果，res中的标记一旦为1，就代表该结果有效
    int[] res;
    // 三维数组，最后一个维度是2，这个数组用来输出斯坦纳树的路径
    //最后一个维度 第一维代表前驱节点 第二位代表子集
    int[][][] pre;
    //resultlist 里面将会存储结果的所有编号
    List<Integer> resultlist;
    //最小斯坦纳树的权值和
    double minDpResult;

    public Graph(Data data) {
        this.data = data;
        initData();
    }
/*
    public void test()
    {
        System.out.println(data.ii.apiCategoryInvertindex.keySet().size());
        System.out.println(data.ii.search("Food",type.API_CATEGORY));
    }

 */


    void initData() {
        ArrayList<API> vertexList = new ArrayList<>();
        for (API api : data.getAPImap().values()) {
            vertexList.add(api);
        }
        API[] vertices = vertexList.toArray(new API[vertexList.size()]);
        vertexList = null;
        this.vertices = vertices;
        //现在，初始化所有结点的index
        for (int i = 0; i < vertices.length; i++) {
            vertices[i].setIndexInGraph(i);
        }
        graphTable = new HashMap<>();
        unweightedgraphTable = new HashMap<>();
        IndexMap = new HashMap<>();
        InvertedIndexMap = new HashMap<>();
        //建立邻接表
        for (Mashup mashup : data.getMashupmap().values()) {
            List<API> apis = mashup.getRelatedApis();
            for (int i = 0; i < apis.size(); i++) {
                for (int j = i + 1; j < apis.size(); j++) {
                    addEdge(apis.get(i), apis.get(j));
                }
            }
        }
        // 邻接表操作，进行1.0/权重
        // 邻接表操作，进行节点编号映射
        // 在图中的不孤立的节点编号将从1开始
        int indexIngraph = 1;
        for(Map.Entry<Integer,Map<Integer,Double>> entry:graphTable.entrySet())
        {
            Integer keyInGraphTable = entry.getKey();
            Map<Integer,Double> table = entry.getValue();
            //这里，我们将进行非孤立节点的编号操作
            IndexMap.put(keyInGraphTable,indexIngraph);
            InvertedIndexMap.put(indexIngraph,keyInGraphTable);
            indexIngraph++;
            for(Map.Entry<Integer,Double> another_entry:table.entrySet())
            {
                Integer key = another_entry.getKey();
                Double value = another_entry.getValue();
                graphTable.get(keyInGraphTable).put(key,1.0/value);
            }
        }

    }
    public void addEdge(API from, API to) {
        addEdge(from.getIndexInGraph(), to.getIndexInGraph());
    }
    public void addEdge(int from, int to) {
        if(graphTable.containsKey(from))
        {
            double length = graphTable.get(from).getOrDefault(to,0.0) + 1;
            graphTable.get(from).put(to,length);
        }
        else{
            graphTable.put(from,new HashMap<>());
            graphTable.get(from).put(to,1.0);
        }

        if(graphTable.containsKey(to))
        {
            double length = graphTable.get(to).getOrDefault(from,0.0) + 1;
            graphTable.get(to).put(from,length);
        }
        else{
            graphTable.put(to,new HashMap<>());
            graphTable.get(to).put(from,1.0);
        }

        if(unweightedgraphTable.containsKey(from))
        {
            unweightedgraphTable.get(from).add(to);
        }
        else{
            unweightedgraphTable.put(from,new HashSet<>());
            unweightedgraphTable.get(from).add(to);
        }

        if(unweightedgraphTable.containsKey(to))
        {
            unweightedgraphTable.get(to).add(from);
        }
        else{
            unweightedgraphTable.put(to,new HashSet<>());
            unweightedgraphTable.get(to).add(from);
        }
    }

    public double getEdgeWeight(API from, API to) {
        return getEdgeWeight(from.getIndexInGraph(), to.getIndexInGraph());
    }

    public double getEdgeWeight(int from, int to) {
        if(graphTable.containsKey(from) == false)
        {
            return 0;
        }
        return graphTable.get(from).getOrDefault(to,0.0);
    }

    // 无权图SPFA
    private void SPFAforUnweighted(int s)
    {
        while(!queue.isEmpty())
        {
            //root在图中的编号（假的编号）
            int root = queue.peek();
            queue.poll();
            vis[root] = 0;
            //找到真正的图中的编号
            int realroot = InvertedIndexMap.get(root);
            Set<Integer> table = unweightedgraphTable.get(realroot);
            for(Integer index:table)
            {
                //做一步变换，找到图中的编号，这是为了防止内存爆炸
                int indexIngraph = IndexMap.get(index);
                // int indexIngraph = key;
                if(dp[indexIngraph][s] > 1 + dp[root][s] )
                {
                    dp[indexIngraph][s] = 1 + dp[root][s];
                    pre[indexIngraph][s][0] = root;
                    pre[indexIngraph][s][1] = s;
                    if(vis[indexIngraph] == 0)
                    {
                        queue.offer(indexIngraph);
                        vis[indexIngraph] = 1;
                    }
                }
            }
        }
    }
    // 有权图的SPFA
    private void SPFA(int s)
    {
        while(!queue.isEmpty())
        {
            //root在图中的编号（假的编号）
            int root = queue.peek();
            queue.poll();
            vis[root] = 0;
            //找到真正的图中的编号
            int realroot = InvertedIndexMap.get(root);
           // int realroot = root;
            Map<Integer,Double> table = graphTable.get(realroot);
            for(Map.Entry<Integer,Double> entry:table.entrySet())
            {
                int key = entry.getKey();
                //做一步变换，找到图中的编号，这是为了防止内存爆炸
                int indexIngraph = IndexMap.get(key);
               // int indexIngraph = key;
                //边的长度
                double length = entry.getValue();
                if(dp[indexIngraph][s] > length + dp[root][s] )
                {
                    dp[indexIngraph][s] = length + dp[root][s];
                    pre[indexIngraph][s][0] = root;
                    pre[indexIngraph][s][1] = s;
                    if(vis[indexIngraph] == 0)
                    {
                        queue.offer(indexIngraph);
                        vis[indexIngraph] = 1;
                    }
                }
            }
        }
    }

    //找节点，对pre数组进行操作
    private void dfs(int u,int now)
    {
        /*
        System.out.print("u:");
        System.out.print(u);
        System.out.print("now:");
        System.out.println(Integer.toBinaryString(now));
         */
        if(pre[u][now][1] == 0)
        {
            res[u] = 1;
            return;
        }
        res[u] = 1;
        if(pre[u][now][0] == u)
        {
            dfs(u,pre[u][now][1]^now);
        }
        dfs(pre[u][now][0],pre[u][now][1]);
    }



    public Set<API> searchbyStenierTree(String[] keywords,InvertedIndex ii,boolean isWeighted)
    {
        //dp数组,第一维只需要开到 Indexmap的大小+1就行，因为我们的编号从1开始，
        // dp第二维需要开到(1<<keyword数组大小)，表示状态
        maxnum = IndexMap.size() + 1;
        type t = type.API_TAGS;
        dp = new double[ maxnum ][1<<(keywords.length)];
        queue =new LinkedList<>();
        vis = new int[maxnum];
        pre = new int[maxnum][1<<(keywords.length)][2];
        res = new int[maxnum];
        dpinit = new int[maxnum];
        Set<API> resultset = new HashSet<>();
        //resultlist已经注释过了，这里面会存储真正的节点编号，最后我们测试的时候会把这个东西取出来，非常重要的结果集
        resultlist = new ArrayList<>();
       // System.out.println(maxnum);
        //dp数组初始化为maxvalue
        for(int i=0;i<dp.length;i++)
        {
            for(int j=0;j<dp[0].length;j++)
            {
                dp[i][j] = Double.MAX_VALUE;
            }
        }


        List<Set<API>> apiSets = new ArrayList<>();
        for (String keyword : keywords) {
            apiSets.add(ii.search(keyword, t));
        }

        int keywordIndex = 1;
        for(Set<API> apiSet:apiSets)
        {
            for(API api:apiSet)
            {
                //真正的index
                int realIndex = api.getIndexInGraph();
                //System.out.println(realIndex);
                // 映射之后的index
                if(IndexMap.containsKey(realIndex))
                {
                    int graphIndex = IndexMap.get(realIndex);
                    //int graphIndex = realIndex;
                    /*
                    System.out.print(graphIndex);
                    System.out.print(" ");
                    System.out.println(realIndex);
                     */
                    //需要记录dpinit数组的值，为了之后进行组合初始化
                    dpinit[graphIndex] |= (1<<(keywordIndex - 1));
                    //这就是状态码了，这样记录只会记录线性空间的状态
                    int status = dpinit[graphIndex];
                    // 1<<(keywordindex-1) 应该正好就是某个关键词对应的状态编号
                    dp[graphIndex][status] = 0;
                    //这里可能会修改，第一次修改可能是因为一个api有多个tag
                    pre[graphIndex][status][1] = 0;
                    pre[graphIndex][status][0] = graphIndex;
                    // 再记录一个单状态的情况，尽力记录，不能开指数级，不然时间复杂度太高
                    dp[graphIndex][1<<(keywordIndex - 1)] = 0;
                    pre[graphIndex][1<<(keywordIndex - 1)][1] = 0;
                    pre[graphIndex][1<<(keywordIndex - 1)][0] = graphIndex;
                    //这样，我们保证尽力满足状态
                }
            }
            keywordIndex++;
        }



        for (int s = 1; s < (1 << keywords.length); s++) {
            for (int i = 1; i <= maxnum-1; i++) {
                //这里进行状态压缩dp,方法是不断进行lowbit操作，类似bit数组那里的操作
                for (int subs = s & (s - 1); subs!=0; subs = s & (subs - 1)) {
                    if(dp[i][s] >dp[i][subs] + dp[i][s ^ subs])
                    {
                        dp[i][s] = dp[i][subs] + dp[i][s ^ subs];// 状压 dp
                        pre[i][s][0] = i;
                        pre[i][s][1] = subs;
                    }
                }
                if(dp[i][s] != Double.MAX_VALUE){
                    queue.offer(i);
                    vis[i] = 1;
                }
            }
            if(isWeighted) {
                SPFA(s);
            }
            else{
                SPFAforUnweighted(s);
            }
        }
        //System.out.println(apiSets);
        //Set<API> firstkeywordAPISets = apiSets.get(0);
        minDpResult = Double.MAX_VALUE;
        int mindpindex = -1;
        // 下面这些代码将会比较所有的 “树根” 所形成的斯坦纳树的大小
        // 外层循环：遍历所有关键词对应的apiSets，由于一个关键词可能对应多个api
        // 内存循环：将每一个api作为“树根”，因为树根的选择是多样化的，这里选使得dp[树根][满状态]最小的那个
        for(Set<API> apiSet:apiSets)
        {
            for(API api:apiSet)
            {
                int realindex = api.getIndexInGraph();
                if(IndexMap.containsKey(realindex))
                {
                    int graphindex = IndexMap.get(realindex);
                    //int graphindex = realindex;
                    //System.out.print(graphindex);
                    //System.out.println(minDpResult);
                    if(dp[graphindex][(1 << keywords.length)-1] < minDpResult)
                    {
                        mindpindex = graphindex;
                        minDpResult = dp[graphindex][(1 << keywords.length)-1];
                    }
                }
            }
        }


        if(mindpindex == -1)
        {
            //如果没找到resultset，有两种情况：
            //1、情况1 由于我们只遍历了“有连边的结点”也就是1236个图中不孤立的结点，所以如果一个mashup内只有一个api，这个api是孤立的，不会进入我们算法的输入
            //   这时候，我们需要遍历所有结点，找到那种孤立结点的解决方案，这时候，只会返回一个孤立结点
            //2  情况2 这些关键词本身就不连通，比如wine和water，那么返回null即可
            for(API api:vertices){
                int tags_length = api.getLowercaseTags().size();
                int tags_cnt = 0;
                List<String> tags = api.getLowercaseTags();
                for(String tag:tags)
                {
                    for(String keyword:keywords)
                    {
                        if(keyword.equalsIgnoreCase(tag))
                        {
                            tags_cnt++;
                            break;
                        }
                    }
                }
                if(tags_cnt == keywords.length)
                {
                    minDpResult = 0;
                    resultset.add(api);
                    resultlist.add(api.getIndexInGraph());
                    return resultset;
                }
            }
            //注意，如果你的所有关键词不是联通的，将会返回null
            return null;
        }
        dfs(mindpindex,(1 << keywords.length)-1);
        for(int i=0;i<res.length;i++)
        {
            if(res[i] == 1)
            {
                int realindex = InvertedIndexMap.get(i);
                resultlist.add(realindex);
            }
        }
        //找出所有api
        resultset = searchVertices(resultlist);
        return resultset;



    }

    //根据真正的节点编号，找到vertice数组（所有api）中对应的api
    public Set<API> searchVertices(List<Integer> resultlist)
    {
        Set<API> resultset =new HashSet<>();
        for(Integer resultIndex:resultlist)
        {
            for(API api:vertices)
            {
                if(api.getIndexInGraph() == resultIndex)
                {
                    resultset.add(api);
                }
            }
        }
        return resultset;
    }



    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append("图中一共有");
        ret.append(IndexMap.size());
        ret.append("个有效节点,也就是说，他们有连边，不是孤立的\n");
        ret.append("API总数为");
        ret.append(vertices.length);
        ret.append("\n");
        ret.append("下面，输出邻接表\n");
        for(Map.Entry<Integer,Map<Integer,Double>> entry:graphTable.entrySet())
       {
           int key = entry.getKey();
           ret.append(key);
           ret.append(":");
           Map<Integer,Double> table = entry.getValue();
           for(Map.Entry<Integer,Double> anotherentry:table.entrySet())
           {
               ret.append("(to:");
               ret.append(anotherentry.getKey());
               ret.append(",");
               ret.append("length:");
               ret.append(anotherentry.getValue());
               ret.append(")");
           }
           ret.append("\n");
       }
        return ret.toString();
    }
}
