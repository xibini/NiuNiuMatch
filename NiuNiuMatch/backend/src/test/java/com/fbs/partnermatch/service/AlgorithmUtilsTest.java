package com.fbs.partnermatch.service;

import com.fbs.partnermatch.utils.AlgorithmUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class AlgorithmUtilsTest {

    @Test
    void test(){
        List<String> tagList1 = Arrays.asList("Java","大一","男");
        List<String> tagList2 = Arrays.asList("c++","大二","女");
        List<String> tagList3 = Arrays.asList("python","大一","男");
        int score1 = AlgorithmUtils.minDistance(tagList1, tagList2);
        int score2 = AlgorithmUtils.minDistance(tagList1, tagList3);
        System.out.println(score1);
        System.out.println(score2);
    }
}
