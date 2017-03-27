package com.ge.apm.domain;

/**
 * Created by lsg on 27/3/2017.
 */
public class Rating {
    double netprofit=0.0f,injectCount=0.0f,exposeCount=0.0f,filmCount=0.0f;

    public double getNetprofit() {
        return netprofit;
    }

    public void setNetprofit(double netprofit) {
        this.netprofit = netprofit;
    }

    public double getInjectCount() {
        return injectCount;
    }

    public void setInjectCount(double injectCount) {
        this.injectCount = injectCount;
    }

    public double getExposeCount() {
        return exposeCount;
    }

    public void setExposeCount(double exposeCount) {
        this.exposeCount = exposeCount;
    }

    public double getFilmCount() {
        return filmCount;
    }

    public void setFilmCount(double filmCount) {
        this.filmCount = filmCount;
    }
}
