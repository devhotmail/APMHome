package com.ge.apm.service.api;

import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import javaslang.Tuple;
import javaslang.Tuple6;
import javaslang.control.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;


@Service
public class OrgService {
  private static final Logger log = LoggerFactory.getLogger(CommonService.class);
  private Database db;

  @Resource(name = "connectionProvider")
  private ConnectionProvider connectionProvider;

  @PostConstruct
  public void init() {
    db = Database.from(connectionProvider);
  }

  public Tuple6<Integer, Integer, Integer, Integer, String, String> find(Integer id) {
    return db.select("select id, parent_id as parent, site_id as site, hospital_id as hospital, name as cn, name_en as en from org_info where id = :id")
      .parameter("id", id)
      .getAs(Integer.class, Integer.class, Integer.class, Integer.class, String.class, String.class)
      .map(t -> Tuple.of(t._1(), Option.of(t._2()).getOrElse(0), Option.of(t._3()).getOrElse(0), Option.of(t._4()).getOrElse(0), Option.of(t._5()).getOrElse(""), Option.of(t._6()).getOrElse("")))
      .cache().toBlocking().single();
  }

  public Iterable<Tuple6<Integer, Integer, Integer, Integer, String, String>> find(Integer site, Integer hospital, Integer start, Integer limit) {
    return db.select("select id, parent_id as parent, site_id as site, hospital_id as hospital, name as cn, name_en as en from org_info where site_id = :site and hospital_id = :hospital offset :start limit :limit")
      .parameter("site", site).parameter("hospital", hospital).parameter("start", start).parameter("limit", limit)
      .getAs(Integer.class, Integer.class, Integer.class, Integer.class, String.class, String.class)
      .map(t -> Tuple.of(t._1(), Option.of(t._2()).getOrElse(0), Option.of(t._3()).getOrElse(0), Option.of(t._4()).getOrElse(0), Option.of(t._5()).getOrElse(""), Option.of(t._6()).getOrElse("")))
      .cache().toBlocking().toIterable();
  }


}
