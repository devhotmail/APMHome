package com.ge.apm.view.analysis;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.ibatis.jdbc.SQL;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.SqlConfigurableChartController;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

@ManagedBean
@ViewScoped
public class WoScheduleController extends SqlConfigurableChartController {

  private static final long serialVersionUID = 1L;
  private static final Logger logger = LoggerFactory.getLogger(WoScheduleController.class);

  private final String username = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
  private final HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
  private final String remote_addr = request.getRemoteAddr();
  private final String page_uri = request.getRequestURI();
  private final int site_id = UserContextService.getCurrentUserAccount().getSiteId();
  private final int hospital_id = UserContextService.getCurrentUserAccount().getHospitalId();

  private final Map<String, String> queries;
  private final JdbcTemplate jdbcTemplate;
  private final Map<Integer, String> eventTypes;
  private final int userId;
  private ScheduleModel model;
  private int inspNum;
  private int meterNum;
  private int mtNum;
  private int pmNum;

  public WoScheduleController() {
    UserAccount user = UserContextService.getCurrentUserAccount();
    jdbcTemplate = WebUtil.getServiceBean("jdbcTemplate", JdbcTemplate.class);
    userId = user.getId();
    queries = Maps.newLinkedHashMap();
    queries.put("inspection", "select distinct io.order_type, io.name, io.start_time, io.end_time, io.create_time, io.creator_name, ai.location_name, io.is_finished from inspection_order_detail iod join inspection_order io on iod.site_id = io.site_id and iod.order_id = io.id join asset_info ai on iod.site_id = ai.site_id and iod.asset_id = ai.id where io.start_time between ? and ? and ai.is_valid = true and ai.site_id = ? and ai.hospital_id = ? and io.owner_id = ? and io.start_time is not null order by io.start_time");
    String mtSql = new SQL() {{
      SELECT("sr.from_dept_name || '设备维修' as name", "sr.created_date as start_time", "sr.close_time as end_time", "sr.requestor_name as creator_name", "ai.location_name", "CASE WHEN sr.status=2 THEN 1 ELSE 0 END as is_closed");
      FROM("v2_service_request sr join asset_info ai on sr.asset_id = ai.id");
      WHERE("sr.created_date between ? and ?");
      WHERE("ai.is_valid = true");
      WHERE("ai.site_id = ?");
      WHERE("ai.hospital_id = ?");
      WHERE("sr.requestor_id= ?");
      WHERE("sr.created_date is not null");
      ORDER_BY("sr.created_date");
    }}.toString();
    queries.put("maintenance", mtSql);
    queries.put("preventiveMaintenance", "select po.name, po.start_time, po.end_time, po.create_time, po.creator_name, ai.location_name, po.is_finished from pm_order po join asset_info ai on po.site_id = ai.site_id and po.hospital_id = ai.hospital_id and po.asset_id = ai.id where po.start_time between ? and ? and ai.is_valid = true and ai.site_id = ? and ai.hospital_id = ? and po.owner_id = ? and po.start_time is not null order by po.start_time");
    queries.put("inspNum", "select count(*) from (select distinct io.order_type, io.name, io.start_time, io.end_time, io.create_time, io.creator_name, ai.location_name, io.is_finished from inspection_order_detail iod join inspection_order io on iod.site_id = io.site_id and iod.order_id = io.id join asset_info ai on iod.site_id = ai.site_id and iod.asset_id = ai.id where ai.is_valid = true and ai.site_id = ? and ai.hospital_id = ? and io.owner_id = ? and io.is_finished = false and io.start_time is not null order by io.start_time) insp where order_type = 1");
    queries.put("meterNum", "select count(*) from (select distinct io.order_type, io.name, io.start_time, io.end_time, io.create_time, io.creator_name, ai.location_name, io.is_finished from inspection_order_detail iod join inspection_order io on iod.site_id = io.site_id and iod.order_id = io.id join asset_info ai on iod.site_id = ai.site_id and iod.asset_id = ai.id where ai.is_valid = true and ai.site_id = ? and ai.hospital_id = ? and io.owner_id = ? and io.is_finished = false and io.start_time is not null order by io.start_time) insp where order_type = 2");
    String mtNum = new SQL() {{
      SELECT("count(*)");
      FROM("v2_service_request sr join asset_info ai on sr.asset_id = ai.id");
      WHERE("ai.site_id = ?");
      WHERE("ai.hospital_id = ?");
      WHERE("sr.requestor_id = ?");
      WHERE("sr.status != 2");
    }}.toString();
    queries.put("mtNum", mtNum);
    queries.put("pmNum", "select count(*) from pm_order po join asset_info ai on po.site_id = ai.site_id and po.hospital_id = ai.hospital_id and po.asset_id = ai.id where ai.is_valid = true and ai.site_id = ? and ai.hospital_id = ? and po.owner_id = ? and po.start_time is not null and po.is_finished = false");
    eventTypes = ImmutableMap.of(1, "巡检", 2, "计量", 3, "质控", 4, "维修", 5, "保养");
  }

  @PostConstruct
  public void init() {
    model = new LazyScheduleModel() {

      private static final long serialVersionUID = 1L;

      @Override
      public void loadEvents(final Date start, Date end) {
        List<Event> inspectionEvents = initInspectionEvents(start, end);
        List<Event> maintenanceEvents = initMaintenanceEvents(start, end);
        List<Event> preventiveMaintenanceEvents = initPreventiveMaintenanceEvents(start, end);
        Iterable<DefaultScheduleEvent> events = FluentIterable.from(inspectionEvents)
          .append(maintenanceEvents)
          .append(preventiveMaintenanceEvents)
          .transform(new Function<Event, DefaultScheduleEvent>() {
            @Override
            public DefaultScheduleEvent apply(Event input) {
              DefaultScheduleEvent event = new DefaultScheduleEvent();
              event.setTitle(input.getTitle());
              event.setStartDate(input.getStart());
              event.setEndDate(Optional.fromNullable(input.getEnd()).or(new DateTime(input.getStart()).plusHours(1).toDate()));
              event.setDescription(input.toHtml());
              event.setData(input);
              event.setAllDay(false);
              event.setEditable(false);
              event.setStyleClass(String.format("event-type-%s %s", input.getTypeId(), input.isClosed() ? "closed" : ""));
              return event;
            }
          });
        for (DefaultScheduleEvent event : events) {
          model.addEvent(event);
        }
        //RequestContext.getCurrentInstance().execute(String.format("updateTopPanel([%s,%s,%s,%s,%s])", inspNum, meterNum, qaNum, mtNum, pmNum));
      }
    };

    String sqlParams = String.format("{_sql=%s, site_id=%s, hospital_id=%s, user_Id=%s}",
      queries.get("inspNum"), site_id, hospital_id, userId);
    logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);
    inspNum = jdbcTemplate.queryForObject(queries.get("inspNum"), Integer.TYPE, site_id, hospital_id, userId);

    sqlParams = String.format("{_sql=%s, site_id=%s, hospital_id=%s, user_Id=%s}",
      queries.get("meterNum"), site_id, hospital_id, userId);
    logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);
    meterNum = jdbcTemplate.queryForObject(queries.get("meterNum"), Integer.TYPE, site_id, hospital_id, userId);

    sqlParams = String.format("{_sql=%s, site_id=%s, hospital_id=%s, user_Id=%s}",
      queries.get("mtNum"), site_id, hospital_id, userId);
    logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);
    mtNum = jdbcTemplate.queryForObject(queries.get("mtNum"), Integer.TYPE, site_id, hospital_id, userId);

    sqlParams = String.format("{_sql=%s, site_id=%s, hospital_id=%s, user_Id=%s}",
      queries.get("pmNum"), site_id, hospital_id, userId);
    logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);
    pmNum = jdbcTemplate.queryForObject(queries.get("pmNum"), Integer.TYPE, site_id, hospital_id, userId);
  }

  private List<Event> initInspectionEvents(Date start, Date end) {

    String sqlParams = String.format("{_sql=%s, site_id=%s, hospital_id=%s, user_Id=%s, startDate=%s, endDate=%s}",
      queries.get("inspection"), site_id, hospital_id, userId, start, end);
    logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);

    return jdbcTemplate.query(queries.get("inspection"), new RowMapper<Event>() {
      @Override
      public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Event(rs.getInt("order_type"), eventTypes.get(rs.getInt("order_type")), rs.getBoolean("is_finished"), rs.getString("name"), rs.getTimestamp("start_time"), Optional.fromNullable(rs.getTimestamp("end_time")).or(new Timestamp(new DateTime(rs.getTimestamp("start_time")).plusHours(1).getMillis())), Optional.fromNullable(rs.getTimestamp("create_time")).or(rs.getTimestamp("start_time")), rs.getString("creator_name"), rs.getString("location_name"));
      }
    }, start, end, site_id, hospital_id, userId);
  }

  private List<Event> initMaintenanceEvents(Date start, Date end) {

    String sqlParams = String.format("{_sql=%s, site_id=%s, hospital_id=%s, user_Id=%s, startDate=%s, endDate=%s}",
      queries.get("maintenance"), site_id, hospital_id, userId, start, end);
    logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);

    return jdbcTemplate.query(queries.get("maintenance"), new RowMapper<Event>() {
      @Override
      public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Event(4, eventTypes.get(4), rs.getBoolean("is_closed"), rs.getString("name"), rs.getTimestamp("start_time"), rs.getTimestamp("end_time"), rs.getTimestamp("start_time"), rs.getString("creator_name"), rs.getString("location_name"));
      }
    }, start, end, site_id, hospital_id, userId);
  }

  private List<Event> initPreventiveMaintenanceEvents(Date start, Date end) {

    String sqlParams = String.format("{_sql=%s, site_id=%s, hospital_id=%s, user_Id=%s, startDate=%s, endDate=%s}",
      queries.get("preventiveMaintenance"), site_id, hospital_id, userId, start, end);
    logger.debug("{} {} {} {} \"{}\" {}", remote_addr, site_id, hospital_id, username, page_uri, sqlParams);

    return jdbcTemplate.query(queries.get("preventiveMaintenance"), new RowMapper<Event>() {
      @Override
      public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Event(5, eventTypes.get(5), rs.getBoolean("is_finished"), rs.getString("name"), rs.getTimestamp("start_time"), Optional.fromNullable(rs.getTimestamp("end_time")).or(new Timestamp(new DateTime(rs.getTimestamp("start_time")).plusHours(1).getMillis())), Optional.fromNullable(rs.getTimestamp("create_time")).or(rs.getTimestamp("start_time")), rs.getString("creator_name"), rs.getString("location_name"));
      }
    }, start, end, site_id, hospital_id, userId);
  }

  private static String from(Date date, String format) {
    return new DateTime(date).toString(DateTimeFormat.forPattern(format));
  }

  public ScheduleModel getModel() {
    return model;
  }

  public int getInspNum() {
    return inspNum;
  }

  public int getMeterNum() {
    return meterNum;
  }

  public int getmtNum() {
    return mtNum;
  }

  public int getPmNum() {
    return pmNum;
  }

  public final static class Event {
    private int typeId;
    private String typeName;
    private boolean isClosed;
    private String title;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date start;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date end;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date created;
    private String creator;
    private String location;

    public Event(int typeId, String typeName, boolean isClosed, String title, Date start, Date end, Date created, String creator, String location) {
      this.typeId = typeId;
      this.typeName = typeName;
      this.isClosed = isClosed;
      this.title = title;
      this.start = start;
      this.end = end;
      this.created = created;
      this.creator = creator;
      this.location = location;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
        .omitNullValues()
        .add("typeId", typeId)
        .add("typeName", typeName)
        .add("isClosed", isClosed)
        .add("title", title)
        .add("start", start)
        .add("end", end)
        .add("created", created)
        .add("creator", creator)
        .add("location", location)
        .toString();
    }

    public String toHtml() {
      return String.format("<div class=\"event-tooltip u-p-\"><div class=\"title u-mb--\">%s</div><div>%s</div><div>%s-%s</div><div class=\"u-mv-\">任务于 %s 由 %s 创建</div><div>%s</div></div>", title, from(start, "yyyy-MM-dd"), from(start, "HH:mm"), from(end, "HH:mm"), from(created, "yyyy-MM-dd"), creator, location);
    }

    public int getTypeId() {
      return typeId;
    }

    public void setTypeId(int typeId) {
      this.typeId = typeId;
    }

    public String getTypeName() {
      return typeName;
    }

    public void setTypeName(String typeName) {
      this.typeName = typeName;
    }

    public boolean isClosed() {
      return isClosed;
    }

    public void setClosed(boolean closed) {
      isClosed = closed;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public Date getStart() {
      return start;
    }

    public void setStart(Date start) {
      this.start = start;
    }

    public Date getEnd() {
      return end;
    }

    public void setEnd(Date end) {
      this.end = end;
    }

    public Date getCreated() {
      return created;
    }

    public void setCreated(Date created) {
      this.created = created;
    }

    public String getCreator() {
      return creator;
    }

    public void setCreator(String creator) {
      this.creator = creator;
    }

    public String getLocation() {
      return location;
    }

    public void setLocation(String location) {
      this.location = location;
    }
  }
}
