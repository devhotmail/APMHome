package com.ge.apm.service.api;

import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.UserAccount;
import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import com.github.davidmoten.rx.jdbc.tuple.Tuple2;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import javaslang.Tuple;
import javaslang.Tuple3;
import javaslang.Tuple7;
import javaslang.collection.HashMap;
import javaslang.control.Option;
import javaslang.control.Try;
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

  @Cacheable(cacheNames = "springCache", key = "'commonService.findAllMessages'")
  public Iterable<Tuple7<Integer, String, String, String, String, String, Integer>> findAllMessages() {
    return Try.of(() -> db.select(new SQL().SELECT("id", "msg_type", "msg_key", "value_zh", "value_en", "value_tw", "site_id").FROM("i18n_message").toString())
      .getAs(Integer.class, String.class, String.class, String.class, String.class, String.class, Integer.class)
      .map(t -> Tuple.of(t._1(), t._2(), t._3(), t._4(), t._5(), t._6(), t._7())).sorted((l, r) -> l._1 - r._1)
      .cache().toBlocking().toIterable()).getOrElse(ImmutableList.of());
  }

  @Cacheable(cacheNames = "springCache", key = "'commonService.findFields.'+#siteId+'.'+#type")
  public Map<String, String> findFields(int siteId, String type) {
    return Try.of(() -> Observable.from(Option.of(DbMessageSource.getMessageCache(siteId)).filter(map -> map.entrySet().stream().anyMatch(entry -> entry.getValue().getMsgType().equalsIgnoreCase(type))).getOrElse(DbMessageSource.getMessageCache(-1)).entrySet())
      .filter(entry -> entry.getValue().getMsgType().equalsIgnoreCase(type))
      .map(Map.Entry::getValue).toMap(I18nMessage::getMsgKey, I18nMessage::getValueZh)
      .cache().toBlocking().single()).getOrElse(ImmutableMap.of());
  }

  @Cacheable(cacheNames = "springCache", key = "'commonService.findHospitals'")
  public Map<Integer, Tuple3<Integer, String, String>> findHospitals() {
    return Try.of(() -> db.select(new SQL().SELECT("id", "name", "alias_name").FROM("site_info").toString())
      .getAs(Integer.class, String.class, String.class)
      .toMap(com.github.davidmoten.rx.jdbc.tuple.Tuple3::_1, t -> Tuple.of(t._1(), Option.of(t._2()).getOrElse(""), Option.of(t._3()).getOrElse("")))
      .cache().toBlocking().single()).getOrElse(ImmutableMap.of());
  }

  @Cacheable(cacheNames = "springCache", key = "'commonService.findDepts.'+#siteId+'.'+#hospitalId")
  public Map<Integer, String> findDepts(int siteId, int hospitalId) {
    return Try.of(() -> db.select(new SQL().SELECT_DISTINCT("clinical_dept_id", "clinical_dept_name").FROM("asset_info").WHERE("site_id = :site_id ").WHERE("hospital_id = :hospital_id").toString())
      .parameter("site_id", siteId).parameter("hospital_id", hospitalId)
      .getAs(Integer.class, String.class)
      .toMap(Tuple2::_1, tuple -> Option.of(tuple._2()).getOrElse(""))
      .cache().toBlocking().single()).getOrElse(ImmutableMap.of());
  }

  @Cacheable(cacheNames = "springCache", key = "'commonService.findParts'")
  public Map<Integer, String> findParts() {
    return Try.of(() -> db.select(new SQL().SELECT("id", "name").FROM("proc_part").toString())
      .getAs(Integer.class, String.class)
      .toMap(Tuple2::_1, tuple -> Option.of(tuple._2()).getOrElse(""))
      .cache().toBlocking().single()).getOrElse(ImmutableMap.of());
  }

  @Cacheable(cacheNames = "springCache", key = "'commonService.findSuppliers.'+#siteId")
  public Map<Integer, String> findSuppliers(int siteId) {
    return Try.of(() -> db.select(new SQL().SELECT("id", "name").FROM("supplier").WHERE("site_id = :site_id").toString())
      .parameter("site_id", siteId).getAs(Integer.class, String.class)
      .toMap(Tuple2::_1, Tuple2::_2)
      .cache().toBlocking().single()).getOrElse(ImmutableMap.of());
  }

  @Cacheable(cacheNames = "springCache", key = "'commonService.findAssets.'+#site+'.'+#hospital")
  public Map<Integer, Tuple7<Integer, Integer, Integer, Integer, Integer, Integer, String>> findAssets(int site, int hospital) {
    return Try.of(() -> db.select(new SQL().SELECT("site_id", "hospital_id", "id", "clinical_dept_id", "asset_group", "supplier_id", "name").FROM("asset_info").WHERE("site_id = :site_id ").WHERE("hospital_id = :hospital_id").toString())
      .parameter("site_id", site).parameter("hospital_id", hospital)
      .getAs(Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, String.class)
      .map(t -> Tuple.of(t._1(), t._2(), t._3(), t._4(), t._5(), t._6(), t._7())).sorted((l, r) -> l._3 - r._3)
      .toMap(Tuple7::_3).cache().toBlocking().single()).getOrElse(ImmutableMap.of());
  }

  @Cacheable(cacheNames = "springCache", key = "'commonService.groupAssetsByType.'+#site+'.'+#hospital")
  public Map<Integer, Integer> groupAssetsByType(int site, int hospital) {
    return Try.of(() -> db.select(new SQL().SELECT("asset_group", "count(*)").FROM("asset_info").WHERE("site_id = :site_id ").WHERE("hospital_id = :hospital_id").GROUP_BY("asset_group").toString())
      .parameter("site_id", site).parameter("hospital_id", hospital)
      .getAs(Integer.class, Integer.class)
      .toMap(Tuple2::_1, Tuple2::_2)
      .cache().toBlocking().single()).getOrElse(ImmutableMap.of());
  }

  @Cacheable(cacheNames = "springCache", key = "'commonService.groupAssetsByDept.'+#site+'.'+#hospital")
  public Map<Integer, Integer> groupAssetsByDept(int site, int hospital) {
    return Try.of(() -> db.select(new SQL().SELECT("clinical_dept_id", "count(*)").FROM("asset_info").WHERE("site_id = :site_id ").WHERE("hospital_id = :hospital_id").GROUP_BY("clinical_dept_id").toString())
      .parameter("site_id", site).parameter("hospital_id", hospital)
      .getAs(Integer.class, Integer.class)
      .toMap(Tuple2::_1, Tuple2::_2)
      .cache().toBlocking().single()).getOrElse(ImmutableMap.of());
  }

  @Cacheable(cacheNames = "springCache", key = "'commonService.groupAssetsBySupplier.'+#site+'.'+#hospital")
  public Map<Integer, Integer> groupAssetsBySupplier(int site, int hospital) {
    return Try.of(() -> db.select(new SQL().SELECT("supplier_id", "count(*)").FROM("asset_info").WHERE("site_id = :site_id ").WHERE("hospital_id = :hospital_id").GROUP_BY("supplier_id").toString())
      .parameter("site_id", site).parameter("hospital_id", hospital)
      .getAs(Integer.class, Integer.class)
      .toMap(Tuple2::_1, Tuple2::_2)
      .cache().toBlocking().single()).getOrElse(ImmutableMap.of());
  }

  public ResponseEntity.BodyBuilder headerUserProfile(ResponseEntity.BodyBuilder builder, UserAccount user, Map<Integer, Tuple3<Integer, String, String>> hospitals) {
    return builder.header("user-profile", JsonMapper.nonEmptyMapper().toJson(HashMap.empty().put("user.id", user.getId()).put("user.name", user.getName()).put("hospital.name", Option.of(hospitals.get(user.getSiteId())).map(t -> !Strings.isNullOrEmpty(t._3) && t._3.length() > t._2.length() ? t._3 : t._2).getOrElse("")).toJavaMap()));
  }
}
