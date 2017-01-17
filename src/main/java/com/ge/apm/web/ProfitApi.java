package com.ge.apm.web;

import com.ge.apm.domain.UserAccount;
import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import com.google.common.collect.ImmutableList;
import javaslang.Tuple;
import javaslang.Tuple3;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import rx.Observable;
import webapp.framework.web.service.UserContext;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.money.Monetary;
import java.util.Locale;


@RestController
@RequestMapping("/profit")
public class ProfitApi {
  private static final Logger log = LoggerFactory.getLogger(ProfitApi.class);
  private Database db;

  @Resource(name = "rxJdbcConnectionProvider")
  private ConnectionProvider rxJdbcConnectionProvider;

  @PostConstruct
  public void init() {
    db = Database.from(rxJdbcConnectionProvider);
  }

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  public Iterable<Tuple3<Integer, String, Money>> calcProfit(@RequestParam(value = "year", required = true) int year,
                                                             @RequestParam(value = "groupby", required = false) String groupBy,
                                                             @RequestParam(value = "type", required = false) String type,
                                                             @RequestParam(value = "dept", required = false) String dept,
                                                             @RequestParam(value = "month", required = false) Integer month,
                                                             @RequestParam(value = "limit", required = false) Integer limit,
                                                             @RequestParam(value = "start", required = false) Integer start) {
    log.info("year:{}, groupby:{}, type:{}, dept:{}, month:{}, limit:{}, start:{}", year, groupBy, type, dept, month, limit, start);
    UserAccount user = UserContext.getCurrentLoginUser();
    return ImmutableList.copyOf(calcProfitGroupByAssets(user.getSiteId(), user.getHospitalId(), year).toBlocking().toIterable());
  }

  private Observable<Tuple3<Integer, String, Money>> calcProfitGroupByAssets(int siteId, int hospitalId, int year) {
    return db.select("select ai.id as asset_id, ai.name as asset_name, sum(ar.price_amount) as revenue from asset_clinical_record ar join asset_info ai on ar.asset_id = ai.id and ar.site_id = :site_id and ar.hospital_id = :hospital_id and extract(year from ar.exam_date) = :year group by ai.id order by revenue desc")
      .parameter("site_id", siteId)
      .parameter("hospital_id", hospitalId)
      .parameter("year", year)
      .getAs(Integer.class, String.class, Double.class)
      .map(tuple3 -> Tuple.of(tuple3.value1(), tuple3.value2(), Money.of(tuple3._3(), Monetary.getCurrency(Locale.CHINA))));
  }


  public final static class Profit {

  }


}
