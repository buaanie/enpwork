package com.graph;

/**
 * Created by ACT-NJ on 2017/9/16.
 */
public class finLen {
    public static void main(String[] args) {
                    //0 1 2 3 4 5 6 7 8 9 10 11 12 13  14 15  16  17   18  19  20  21  22
        int[] ints = {4,4,4,4,4,5,6,7,7,8, 9, 9, 9, 9, 10, 13, 14, 14, 16, 17, 17, 17, 20};
        int k =4;
        int m = findL(ints,k);
        System.out.println(m);

        int n = findR(ints,k);
        System.out.println(n);

    }

    private static int findR(int[] ints,int k) {
        int i = 0,j=ints.length-1,mid=0;
        while(i<j){
            mid = (i+j)>>>1;
            if(ints[mid]>k)
                j=mid-1;
            else
                i=mid+1;
        }
        if(ints[i]==k)
            return i;
        else return -1;
    }

    private static int findL(int[] ints,int k) {
        int i = 0,j=ints.length-1,mid=0;
        while(i<j){
            mid = (i+j)>>>1;
            if(ints[mid]>=k)
                j=mid-1;
            else
                i=mid+1;
        }
        if(ints[i]==k)
            return i;
        else return -1;
    }
}
