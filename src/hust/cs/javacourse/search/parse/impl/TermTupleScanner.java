package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.index.impl.TermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleScanner;

import java.io.BufferedReader;
import java.io.IOException;

public class TermTupleScanner extends AbstractTermTupleScanner {
    public TermTupleScanner(BufferedReader input) {
        super(input);
    }

    /**
     * 获得下一个三元组
     *
     * @return: 下一个三元组；如果到了流的末尾，返回null
     */
    @Override
    public AbstractTermTuple next() {
        AbstractTermTuple termTuple = new TermTuple();
        try {
            String str = input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
