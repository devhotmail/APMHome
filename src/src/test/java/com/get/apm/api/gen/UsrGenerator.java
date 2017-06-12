package com.get.apm.api.gen;


import com.get.apm.api.db.AbstractDbTest;
import javaslang.Tuple;
import javaslang.collection.List;
import javaslang.control.Option;
import org.apache.ibatis.jdbc.SQL;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UsrGenerator extends AbstractDbTest {
  private final Logger log = LoggerFactory.getLogger(UsrGenerator.class);
  private Map<Integer, String> orgs;
  private final String salt = "b380e7bb58d700d5";
  private final String pwd = "092561820bad0790cd3b3d5546809287c0cb17e67e6b8dd28665b292560298da";
  private final String sql = new SQL().INSERT_INTO("user_account")
    .VALUES("site_id", ":site_id")
    .VALUES("hospital_id", ":hospital_id")
    .VALUES("org_id", ":org_id")
    .VALUES("login_name", ":login_name")
    .VALUES("name", ":name")
    .VALUES("pwd_salt", ":pwd_salt")
    .VALUES("password", ":password")
    .VALUES("is_super_admin", ":is_super_admin")
    .VALUES("is_site_admin", ":is_site_admin")
    .VALUES("is_local_admin", ":is_local_admin")
    .VALUES("is_active", ":is_active")
    .VALUES("is_online", ":is_online")
    .VALUES("email", ":email")
    .toString();

  @Before
  public void setUp() throws SQLException {
    super.setUp();
    orgs = StreamSupport.stream(findOrgs(1).spliterator(), false).collect(Collectors.toMap(t -> t._1, t -> t._5));
    db.update("truncate table user_account cascade").execute();
  }

  @Test
  public void gen() {
    Observable.from(List.range(1, 10).map(i -> Tuple.of(1, 1, 1, String.format("admin%s", i), String.format("admin%s", i), salt, pwd)).toJavaList())
      .subscribe(r -> db.update(sql)
        .parameter("site_id", r._1)
        .parameter("hospital_id", r._2)
        .parameter("org_id", r._3)
        .parameter("login_name", r._4)
        .parameter("name", r._5)
        .parameter("pwd_salt", r._6)
        .parameter("password", r._7)
        .parameter("is_super_admin", true)
        .parameter("is_site_admin", true)
        .parameter("is_local_admin", true)
        .parameter("is_active", true)
        .parameter("is_online", false)
        .parameter("email", "")
        .execute()
      );

    db.select("select id, parent_id as parent, site_id as site, hospital_id as hospital, name as cn, name_en as en from org_info where site_id = :site and parent_id is not NULL").parameter("site", 1)
      .getAs(Integer.class, Integer.class, Integer.class, Integer.class, String.class, String.class)
      .map(t -> Tuple.of(t._1(), Option.of(t._2()).getOrElse(0), Option.of(t._3()).getOrElse(0), Option.of(t._4()).getOrElse(0), Option.of(t._5()).getOrElse(""), Option.of(t._6()).getOrElse("")))
      .cache()
      .flatMap(t -> Observable.from(List.range(1, 10).map(i -> Tuple.of(1, 1, t._1, String.format("%s_user%s", t._1, i), String.format("%s_user%s", t._1, i), salt, pwd)).toJavaList()))
      .subscribe(r -> db.update(sql)
        .parameter("site_id", r._1)
        .parameter("hospital_id", r._2)
        .parameter("org_id", r._3)
        .parameter("login_name", r._4)
        .parameter("name", r._5)
        .parameter("pwd_salt", r._6)
        .parameter("password", r._7)
        .parameter("is_super_admin", false)
        .parameter("is_site_admin", false)
        .parameter("is_local_admin", false)
        .parameter("is_active", true)
        .parameter("is_online", false)
        .parameter("email", "")
        .execute()
      );
  }


}
