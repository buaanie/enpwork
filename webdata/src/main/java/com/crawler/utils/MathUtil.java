package com.crawler.utils;

/**
 * Created by ACT-NJ on 2017/8/27.
 */
public class MathUtil {
    public static float[] addVec(float[] v1,float[] v2){
//        if(null==v2)
//            return v1;
        int l1 = v1.length;
        int l2 =v2.length;
        if(l1!=l2)
            return null;
        for(int i=0;i<l1;i++){
            v1[i] = v1[i]+v2[i];
        }
        return v1;
    }
    public static float[] minusVec(float[] v1,float[] v2){
        int l1 = v1.length;
        int l2 =v2.length;
        if(l1!=l2)
            return null;
        for(int i=0;i<l1;i++){
            v1[i] = v1[i]-v2[i];
        }
        return v1;
    }
    public static double cosineSimilarity(float[] v1,float[] v2){
        int l1 = v1.length;
        int l2 =v2.length;
        if(l1!=l2)
            return 0;
        double x = 0,m=0,n=0;
        for(int i=0;i<l1;i++){
            x+=v1[i]*v2[i];
//            m+=Math.pow(v1[i],2);
//            n+=Math.pow(v2[i],2); 计算速度较慢
            m+=v1[i]*v1[i];
            n+=v2[i]*v2[i];
        }
        if(m==0 || n==0)
            return 0;
        else
            return x/(Math.sqrt(m*n));//更精确
//            return x/(Math.sqrt(m)*Math.sqrt(n));
    }
    public static float[] normalize(float[] v,int n){
        for(int i =0;i<v.length;i++)
            v[i]=v[i]/n;
        return v;
    }
}
