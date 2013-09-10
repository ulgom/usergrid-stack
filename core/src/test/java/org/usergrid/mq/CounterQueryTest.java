package org.usergrid.mq;

import org.junit.Test;
import org.usergrid.persistence.CounterResolution;
import java.util.List;
import java.util.Map;

import org.usergrid.mq.Query.CounterFilterPredicate;
import org.usergrid.utils.JsonUtils;

import static org.usergrid.utils.ClassUtils.cast;
import static org.usergrid.utils.MapUtils.toMapList;

/**
 * Created with IntelliJ IDEA.
 * User: ApigeeCorporation
 * Date: 9/4/13
 * Time: 10:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class CounterQueryTest {

    @Test
    public void testOrgPOSTParams() {
        CounterQuery cq = new CounterQuery();
        cq.addCategory("category");
        cq.addCounterFilter("counter");
        int l = cq.getLimit();
        cq.setLimit(l);
        Long ft = cq.getFinishTime();
        cq.setFinishTime(ft);
        Long st = cq.getStartTime();
        cq.setStartTime(st);
        CounterResolution cr = cq.getResolution();
        cq.setResolution(cr);
        cq.withStartTime(st);
        cq.withFinishTime(ft);
        List<String> c= cq.getCategories();
        cq.setCategories(c);
        cq.withResolution(cr) ;
        cq.withCategories(c) ;
        cq.withLimit(l)  ;
        boolean ls = cq.isLimitSet();
        boolean pad = cq.isPad();
        cq.withPad(pad) ;
        cq.setPad(pad);
        List<CounterFilterPredicate> cfp = cq.getCounterFilters();
        cq.withCounterFilters(cfp) ;
        cq.setCounterFilters(cfp);
        CounterQuery cq1 = new CounterQuery(cq);
        cq1.newQueryIfNull(null);
        cq1.newQueryIfNull(cq);
        cq1.fromJsonString("");
        //cq1.addCounterFilter(null);
        cq1.getLimit(5)  ;
       // Object o = JsonUtils.parse("a/b/c");
        //Map<String, List<String>> params = cast(toMapList((Map) o));
        //cq1.fromQueryParams(params);
    }
}
