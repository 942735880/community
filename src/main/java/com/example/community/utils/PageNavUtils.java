package com.example.community.utils;

public class PageNavUtils {
    public static int[] pageNav(int current, int totalPage, int navSize){
        //算出current左边有多少个
        int before = navSize / 2;

        //起始的页码，防止了起始为负数
        int start = current-before < 1 ? 1 : current-before;
        //终止的页码
        int end = start+navSize - 1;
        //如果终止页码大于等于总页码
        if (end>=totalPage){
            //总页码就是终止页码
            end = totalPage;
            //因为终止页码变动，起始页码也需要变动
            start = end-navSize + 1;
            //如果总页码小于展示页码个数的话，起始页码可能是负数，将它变为1
            if (start < 1){
                start = 1;
            }
        }

        int[] navs = new int[totalPage<navSize?totalPage:navSize];
        for (int i = start,j = 0; i <= end; i++,j++) {
            navs[j] = i;
        }
        return navs;
    }
}
