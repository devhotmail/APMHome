package com.ge.apm.service.api;

import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.Tuple5;
import javaslang.collection.Stream;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import rx.Observable;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ExamSummitService {
  private final List<LocalDate> dates = Stream.iterate(LocalDate.now().minusYears(3), d -> d.plusDays(1)).takeUntil(date -> date.isAfter(LocalDate.now())).toJavaList();
  private final String sql = new SQL().INSERT_INTO("exam_summit")
    .VALUES("site_id", ":siteId")
    .VALUES("hospital_id", ":hospitalId")
    .VALUES("asset_id", ":assetId")
    .VALUES("dept_id", ":deptId")
    .VALUES("asset_group", ":assetGroup")
    .VALUES("part_id", ":partId")
    .VALUES("subpart_id", ":subpartId")
    .VALUES("step_id", ":stepId")
    .VALUES("exam_count", ":examCount")
    .VALUES("created", ":created")
    .VALUES("last_modified", ":modified")
    .toString();
  private Database db;
  private Observable<Tuple5<Integer, Integer, Integer, Integer, Integer>> assets;
  private Observable<Tuple2<Integer, Integer>> partSteps;

  @Resource(name = "connectionProvider")
  private ConnectionProvider connectionProvider;

  @PostConstruct
  public void init() {
    db = Database.from(connectionProvider);
    assets = db.select(new SQL().SELECT("site_id", "hospital_id", "id", "asset_group", "clinical_dept_id").FROM("asset_info").ORDER_BY("id").toString()).getAs(Integer.class, Integer.class, Integer.class, Integer.class, Integer.class).map(t -> Tuple.of(t._1(), t._2(), t._3(), t._4(), t._5())).cache();
    partSteps = db.select(new SQL().SELECT("part_id", "id").FROM("proc_step").ORDER_BY("id").toString()).getAs(Integer.class, Integer.class).map(t -> Tuple.of(t._1(), t._2())).cache();
    db.update(new SQL().DELETE_FROM("exam_summit").toString()).count().toBlocking().single();
  }

  @Async
  public void testGenerate() {
    Observable.from(dates)
      .flatMap(date -> assets.map(a -> new ClinicalSummit(a._1, a._2, a._3, a._5, a._4, 0, 0, 0, 0, date, date))).cache()
      .flatMap(c -> partSteps.map(p -> new ClinicalSummit(c.getSiteId(), c.getHospitalId(), c.getAssetId(), c.getDeptId(), c.getAssetGroup(), p._1, 0, p._2, 0, c.getCreated(), c.getLastModified())))
      .subscribe(d -> db.update(sql)
        .parameter("siteId", d.getSiteId())
        .parameter("hospitalId", d.getHospitalId())
        .parameter("assetId", d.getAssetId())
        .parameter("deptId", d.getDeptId())
        .parameter("assetGroup", d.getAssetGroup())
        .parameter("partId", d.getPartId())
        .parameter("subpartId", d.getSubpartId())
        .parameter("stepId", d.getStepId())
        .parameter("examCount", ThreadLocalRandom.current().nextInt(0, 3))
        .parameter("created", d.getCreated())
        .parameter("modified", d.getLastModified())
        .returnGeneratedKeys()
        .getAs(Integer.class)
        .toBlocking()
        .single()
      );
  }


  public static class ClinicalSummit {
    private int siteId;
    private int hospitalId;
    private int assetId;
    private int deptId;
    private int assetGroup;
    private int partId;
    private int subpartId;
    private int stepId;
    private int examCount;
    private LocalDate created;
    private LocalDate lastModified;

    public ClinicalSummit(int siteId, int hospitalId, int assetId, int deptId, int assetGroup, int partId, int subpartId, int stepId, int examCount, LocalDate created, LocalDate lastModified) {
      this.siteId = siteId;
      this.hospitalId = hospitalId;
      this.assetId = assetId;
      this.deptId = deptId;
      this.assetGroup = assetGroup;
      this.partId = partId;
      this.subpartId = subpartId;
      this.stepId = stepId;
      this.examCount = examCount;
      this.created = created;
      this.lastModified = lastModified;
    }

    public int getSiteId() {
      return siteId;
    }

    public void setSiteId(int siteId) {
      this.siteId = siteId;
    }

    public int getHospitalId() {
      return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
      this.hospitalId = hospitalId;
    }

    public int getAssetId() {
      return assetId;
    }

    public void setAssetId(int assetId) {
      this.assetId = assetId;
    }

    public int getDeptId() {
      return deptId;
    }

    public void setDeptId(int deptId) {
      this.deptId = deptId;
    }

    public int getAssetGroup() {
      return assetGroup;
    }

    public void setAssetGroup(int assetGroup) {
      this.assetGroup = assetGroup;
    }

    public int getPartId() {
      return partId;
    }

    public void setPartId(int partId) {
      this.partId = partId;
    }

    public int getSubpartId() {
      return subpartId;
    }

    public void setSubpartId(int subpartId) {
      this.subpartId = subpartId;
    }

    public int getStepId() {
      return stepId;
    }

    public void setStepId(int stepId) {
      this.stepId = stepId;
    }

    public int getExamCount() {
      return examCount;
    }

    public void setExamCount(int examCount) {
      this.examCount = examCount;
    }

    public LocalDate getCreated() {
      return created;
    }

    public void setCreated(LocalDate created) {
      this.created = created;
    }

    public LocalDate getLastModified() {
      return lastModified;
    }

    public void setLastModified(LocalDate lastModified) {
      this.lastModified = lastModified;
    }
  }


}
