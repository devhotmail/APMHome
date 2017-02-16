package com.ge.apm.service.api;

import com.ge.apm.domain.I18nMessage;
import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import com.google.common.primitives.Ints;
import javaslang.control.Option;
import org.apache.ibatis.jdbc.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import rx.Observable;
import webapp.framework.web.service.DbMessageSource;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;

@Service
public class CommonService {
  private static final Logger log = LoggerFactory.getLogger(CommonService.class);
  private Database db;

  @Resource(name = "connectionProvider")
  private ConnectionProvider connectionProvider;

  @PostConstruct
  public void init() {
    db = Database.from(connectionProvider);
  }


  public Observable<Map<Integer, String>> findFields(int siteId, String type) {
    return Observable.from(Option
      .of(DbMessageSource.getMessageCache(siteId)).filter(map -> map.entrySet().stream().anyMatch(entry -> entry.getValue().getMsgType().equalsIgnoreCase(type))).getOrElse(DbMessageSource.getMessageCache(-1)).entrySet())
      .filter(entry -> entry.getValue().getMsgType().equalsIgnoreCase(type))
      .map(Map.Entry::getValue)
      .toMap(msg -> Ints.tryParse(msg.getMsgKey()), I18nMessage::getValueZh)
      .cache();
  }

  public Observable<Map<Integer, String>> findDepts(int siteId, int hospitalId) {
    return db
      .select(
        new SQL().SELECT_DISTINCT("clinical_dept_id", "clinical_dept_name")
          .FROM("asset_info")
          .WHERE("site_id = :site_id ")
          .WHERE("hospital_id = :hospital_id").toString())
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId)
      .getAs(Integer.class, String.class)
      .toMap(com.github.davidmoten.rx.jdbc.tuple.Tuple2::_1, tuple -> Option.of(tuple._2()).getOrElse(""))
      .cache();
  }
}
