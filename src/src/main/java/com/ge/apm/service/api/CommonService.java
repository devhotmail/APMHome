package com.ge.apm.service.api;

import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.UserAccount;
import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import com.github.davidmoten.rx.jdbc.tuple.Tuple2;
import com.google.common.base.Strings;
import javaslang.Tuple;
import javaslang.Tuple3;
import javaslang.collection.HashMap;
import javaslang.control.Option;
import org.apache.ibatis.jdbc.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import rx.Observable;
import webapp.framework.broker.JsonMapper;
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

  public Map<String, String> findFields(int siteId, String type) {
    return Observable.from(DbMessageSource.getMessageCache(siteId).entrySet())
      .filter(entry -> entry.getValue().getMsgType().equalsIgnoreCase(type))
      .map(Map.Entry::getValue)
      .toMap(I18nMessage::getMsgKey, I18nMessage::getValueZh)
      .toBlocking()
      .single();
  }

  @Cacheable(cacheNames = "springCache", key = "'commonService.findHospitals'")
  public Map<Integer, Tuple3<Integer, String, String>> findHospitals() {
    return db
      .select(new SQL().SELECT("id", "name", "alias_name").FROM("site_info").toString())
      .getAs(Integer.class, String.class, String.class)
      .toMap(com.github.davidmoten.rx.jdbc.tuple.Tuple3::_1, t -> Tuple.of(t._1(), Option.of(t._2()).getOrElse(""), Option.of(t._3()).getOrElse("")))
      .toBlocking()
      .single();
  }

  @Cacheable(cacheNames = "springCache", key = "'commonService.findDepts.'+#siteId+'.'+#hospitalId")
  public Map<Integer, String> findDepts(int siteId, int hospitalId) {
    return db
      .select(new SQL().SELECT_DISTINCT("clinical_dept_id", "clinical_dept_name").FROM("asset_info").WHERE("site_id = :site_id ").WHERE("hospital_id = :hospital_id").toString())
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId)
      .getAs(Integer.class, String.class)
      .toMap(Tuple2::_1, tuple -> Option.of(tuple._2()).getOrElse(""))
      .toBlocking()
      .single();
  }

  @Cacheable(cacheNames = "springCache", key = "'commonService.findSuppliers.'+#siteId")
  public Map<Integer, String> findSuppliers(int siteId) {
    return db
      .select(new SQL().SELECT("id", "name").FROM("supplier").WHERE("site_id = :site_id").toString())
      .parameter("site_id", siteId).getAs(Integer.class, String.class)
      .toMap(Tuple2::_1, Tuple2::_2)
      .toBlocking()
      .single();
  }

  public ResponseEntity.BodyBuilder headerUserProfile(ResponseEntity.BodyBuilder builder, UserAccount user, Map<Integer, Tuple3<Integer, String, String>> hospitals) {
    return builder.header("user-profile", JsonMapper.nonEmptyMapper().toJson(HashMap.empty().put("user.id", user.getId()).put("user.name", user.getName()).put("hospital.name", Option.of(hospitals.get(user.getSiteId())).map(t -> !Strings.isNullOrEmpty(t._3) && t._3.length() > t._2.length() ? t._3 : t._2).getOrElse("")).toJavaMap()));
  }
}
