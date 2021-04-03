package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * AbstractIndex的具体实现类
 */
public class Index extends AbstractIndex {
    /**
     * 返回索引的字符串表示
     *
     * @return 索引的字符串表示
     */
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("docId->docPath\n");
        for(Map.Entry<Integer,String> entry:docIdToDocPathMapping.entrySet())
        {
            buffer.append(entry.getKey());
            buffer.append("->");
            buffer.append(entry.getValue());
            buffer.append("\n");
        }

        buffer.append("Term->PostingList\n");
        for(Map.Entry<AbstractTerm,AbstractPostingList> entry: termToPostingListMapping.entrySet())
        {
            buffer.append(entry.getKey().toString());
            buffer.append("->");
            buffer.append(entry.getValue().toString());
            buffer.append("\n");
        }
        return buffer.toString();
    }

    /**
     * 添加文档到索引，更新索引内部的HashMap
     *
     * @param document ：文档的AbstractDocument子类型表示
     */
    @Override
    public void addDocument(AbstractDocument document) {
        docIdToDocPathMapping.put(document.getDocId(),document.getDocPath());

        for(AbstractTermTuple tuple :document.getTuples())
        {
            //新的单词
            if(!termToPostingListMapping.containsKey(tuple.term))
            {
                Posting posting = new Posting();
                posting.setDocId(document.getDocId());
                posting.setFreq(tuple.freq);
                List<Integer> position = new ArrayList<>();
                position.add(tuple.curPos);
                posting.setPositions(position);

                PostingList postingList = new PostingList();
                postingList.add(posting);

                termToPostingListMapping.put(tuple.term,postingList);
            }
            else
            {
                int i;
                int size= termToPostingListMapping.get(tuple.term).size();
                for(i =0;i<size;++i)
                {
                    if(termToPostingListMapping.get(tuple.term).get(i).getDocId()==document.getDocId())
                        break;
                }
                if(i==size)     //单词出现在新的文件中
                {
                    Posting posting = new Posting();
                    posting.setDocId(document.getDocId());
                    posting.setFreq(tuple.freq);
                    List<Integer> position = new ArrayList<>();
                    position.add(tuple.curPos);
                    posting.setPositions(position);

                    termToPostingListMapping.get(tuple.term).add(posting);
                }
                else
                {
                    int freq = termToPostingListMapping.get(tuple.term).get(i).getFreq();
                    termToPostingListMapping.get(tuple.term).get(i).setFreq(freq+1);
                    termToPostingListMapping.get(tuple.term).get(i).getPositions().add(tuple.curPos);
                }
            }
        }
    }

    /**
     * <pre>
     * 从索引文件里加载已经构建好的索引.内部调用FileSerializable接口方法readObject即可
     * @param file ：索引文件
     * </pre>
     */
    @Override
    public void load(File file) {
        if(file==null)  return;
        else
        {
            try {
                readObject(new ObjectInputStream(new FileInputStream(file)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * <pre>
     * 将在内存里构建好的索引写入到文件. 内部调用FileSerializable接口方法writeObject即可
     * @param file ：写入的目标索引文件
     * </pre>
     */
    @Override
    public void save(File file) {
        try {
            writeObject(new ObjectOutputStream(new FileOutputStream(file)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回指定单词的PostingList
     *
     * @param term : 指定的单词
     * @return ：指定单词的PostingList;如果索引字典没有该单词，则返回null
     */
    @Override
    public AbstractPostingList search(AbstractTerm term) {
        return termToPostingListMapping.get(term);
    }

    /**
     * 返回索引的字典.字典为索引里所有单词的并集
     *
     * @return ：索引中Term列表
     */
    @Override
    public Set<AbstractTerm> getDictionary() {
        return termToPostingListMapping.keySet();
    }

    /**
     * <pre>
     * 对索引进行优化，包括：
     *      对索引里每个单词的PostingList按docId从小到大排序
     *      同时对每个Posting里的positions从小到大排序
     * 在内存中把索引构建完后执行该方法
     * </pre>
     */
    @Override
    public void optimize() {
        for(Map.Entry<AbstractTerm,AbstractPostingList> entry:termToPostingListMapping.entrySet())
        {
            //对position排序
            for(int i =0;i<entry.getValue().size();++i)
            {
                entry.getValue().get(i).sort();
            }
            //对docId排序
            entry.getValue().sort();
        }
    }

    /**
     * 根据docId获得对应文档的完全路径名
     *
     * @param docId ：文档id
     * @return : 对应文档的完全路径名
     */
    @Override
    public String getDocName(int docId) {
        return docIdToDocPathMapping.get(docId);
    }

    /**
     * 写到二进制文件
     *
     * @param out :输出流对象
     */
    @Override
    public void writeObject(ObjectOutputStream out) {
        try
        {
            out.writeObject(docIdToDocPathMapping);
            out.writeObject(termToPostingListMapping);
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 从二进制文件读
     *
     * @param in ：输入流对象
     */
    @Override
    public void readObject(ObjectInputStream in) {
        try {
            this.docIdToDocPathMapping=(Map<Integer,String>) in.readObject();
            this.termToPostingListMapping=(Map<AbstractTerm,AbstractPostingList>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
