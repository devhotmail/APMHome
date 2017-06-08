package com.get.apm.api.gen;

import com.get.apm.api.db.AbstractDbTest;
import com.github.davidmoten.rx.jdbc.tuple.Tuple2;
import com.google.common.primitives.Ints;
import javaslang.Tuple;
import javaslang.Tuple3;
import org.apache.ibatis.jdbc.SQL;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Ignore
public class ProcGenerator extends AbstractDbTest {
  private final Logger log = LoggerFactory.getLogger(ProcGenerator.class);
  private final String sql = new SQL().INSERT_INTO("proc_map")
    .VALUES("asset_group", ":asset_group")
    .VALUES("part_id", ":part_id")
    .VALUES("step_id", ":step_id")
    .toString();

  private Map<Integer, String> assetGroups;
  private Map<Integer, String> parts;
  private Map<Integer, Collection<Integer>> subparts;
  private Map<Integer, Collection<Integer>> steps;

  @Before
  public void setUp() throws SQLException {
    super.setUp();
    db.update("delete from proc_map").execute();
    assetGroups = StreamSupport.stream(findMsgs(-1, "assetGroup").spliterator(), false).collect(Collectors.toMap(t -> Ints.tryParse(t._3), t -> t._4));
    parts = db.select(new SQL().SELECT("id", "name").FROM("proc_part").toString()).getAs(Integer.class, String.class).toMap(Tuple2::_1, Tuple2::_2).toBlocking().single();
    subparts = db.select(new SQL().SELECT("id", "part_id", "name").FROM("proc_subpart").toString()).getAs(Integer.class, Integer.class, String.class).map(t -> Tuple.of(t._1(), t._2(), t._3())).toMultimap(Tuple3::_2, Tuple3::_1).toBlocking().single();
    steps = db.select(new SQL().SELECT("id", "part_id", "name").FROM("proc_step").toString()).getAs(Integer.class, Integer.class, String.class).map(t -> Tuple.of(t._1(), t._2(), t._3())).toMultimap(Tuple3::_2, Tuple3::_1).toBlocking().single();
  }

  @Test
  public void gen() {
    Observable.from(assetGroups.entrySet().stream().map(Map.Entry::getKey).sorted().collect(Collectors.toList()))
      .flatMap(a -> Observable.from(parts.entrySet().stream().map(b -> Tuple.of(a, b.getKey())).sorted().collect(Collectors.toList())))
      .flatMap(c -> Observable.from(steps.get(c._2).stream().map(i -> Tuple.of(c._1, c._2, i)).collect(Collectors.toList())))
      .subscribe(t -> db.update(sql).parameter("asset_group", t._1).parameter("part_id", t._2).parameter("step_id", t._3).execute()
      );
  }
}
