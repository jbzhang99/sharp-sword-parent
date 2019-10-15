package com.dili.ss;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

public class TestPhantomReference {
    private static ReferenceQueue<Object> rq = new ReferenceQueue<Object>();
    public static void main(String[] args){

        Object obj = new Object();
        PhantomReference<Object> pr = new PhantomReference<Object>(obj, rq);
        System.out.println(pr.get());
        obj = null;
        rq.poll();
        System.gc();
        System.out.println(pr.get());
        Reference<Object> r = (Reference<Object>)rq.poll();
        if(r!=null){
            System.out.println("回收");
        }
    }
}
