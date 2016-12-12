require 'pg'
require 'date'
require 'securerandom'

# configurations

# DB connection info
dbname  = "ge_apm"
user    = "postgres"
password  = "root"
hostaddr  = "127.0.0.1"
table     = "debug"

# data generation
startdate = "2013-1-1"
enddate   = DateTime.now

type      = ["CT", "MR", "xRay", "DR"]
totalrecords  = 6
# totalrecords  = 5



conn = PG::Connection.new(:hostaddr => hostaddr, :dbname => dbname,
                          :user => user, :password => password)

stime = DateTime.parse(startdate).to_time
etime = enddate.to_time
duration  = etime.to_i - stime.to_i

randnum = Random.new()
rand2   = Random.new(123456)


table = "org_info"
totalrecords = 10
for i in 0..totalrecords
  site_id = randnum.rand(1..2)
  name    = "Hosptial-#{i}"

  sql = "insert into #{table} (site_id, name) values (\'#{site_id}\', \'#{name}\')"
  puts sql
  conn.exec(sql)
end

table = "asset_info"
asset_accounts = 90
totalrecords = asset_accounts
for i in 0..totalrecords
  site_id = randnum.rand(1..2)
  asset_group   = randnum.rand(1..4)
  name    = "#{type[asset_group-1]}-#{i+1}"

  function_type = randnum.rand(1..10)
  hospital_id   = [1,2,2,3].sample

  clinical_dept_id = [1,2,3,4,4,4,4,4,5].sample

  asset_dept_id = 100
  asset_owner_id = randnum.rand(1..5)
  asset_owner_name  = "asset-owner-#{asset_owner_id}"
  is_valid      = true
  status        = randnum.rand(1..3)

  location_code = clinical_dept_id
  location_name = ["一楼","二楼","三楼","四楼","五楼"][location_code - 1]

  randtime      = (duration * rand).to_i + etime.to_i
  warranty_date = Time.at(randtime).strftime("%F")

  randtime      = (duration * rand).to_i + stime.to_i
  install_date = Time.at(randtime).strftime("%F")

  randtime      = (duration * rand).to_i + stime.to_i
  last_pm_date = Time.at(randtime).strftime("%F")

  randtime      = (duration * rand).to_i + stime.to_i
  last_metering_date = Time.at(randtime).strftime("%F")

  randtime      = (duration * rand).to_i + stime.to_i
  last_qa_date = Time.at(randtime).strftime("%F")

  sql = "insert into #{table} (site_id, name, function_type, hospital_id, clinical_dept_id, asset_group, asset_dept_id,
        asset_owner_id, asset_owner_name, location_code, location_name,
        is_valid, status, warranty_date, last_pm_date, last_metering_date, last_qa_date, install_date) values (\'#{site_id}\', \'#{name}\',
        \'#{function_type}\', \'#{hospital_id}\', \'#{clinical_dept_id}\', \'#{asset_group}\', \'#{asset_dept_id}\', \'#{asset_owner_id}\', \'#{asset_owner_name}\',
        \'#{location_code}\', \'#{location_name}\', \'#{is_valid}\', \'#{status}\', \'#{warranty_date}\', \'#{last_pm_date}\', \'#{last_metering_date}\', \'#{last_qa_date}\', \'#{install_date}\')"
  puts sql
  conn.exec(sql)
end

table = "asset_depreciation"
totalrecords = 60
for i in 0..totalrecords
  site_id = randnum.rand(1..2)
  asset_id = i + 1

  randtime      = (duration * rand).to_i + stime.to_i
  deprecate_date = Time.at(randtime).strftime("%F")
  deprecate_amount = [1000, 2000, 3000, 4000, 5000].sample

  sql = "insert into #{table} (site_id, asset_id, deprecate_date, deprecate_amount)
        values (\'#{site_id}\', \'#{asset_id}\', \'#{deprecate_date}\', \'#{deprecate_amount}\')"
  puts sql
  conn.exec(sql)
end

table = "work_order"
totalrecords = 10000
for i in 1..totalrecords
  site_id   = randnum.rand(1..2)
  hospital_id = [1, 2, 2, 3].sample
  asset_id  = randnum.rand(1..asset_accounts)
  name      = "repair-asset-#{asset_id}"
  asset_name = "asset-#{asset_id}"
  creator_id    = randnum.rand(1..10)
  creator_name  = "creator-#{creator_id}"

  randtime      = (duration * rand).to_i + stime.to_i
  create_time   = Time.at(randtime).strftime("%F %T")

  requestor_id    = randnum.rand(1..10)
  requestor_name  = "requestor-#{requestor_id}"
  request_time  = Time.at(randtime + randnum.rand(600..1800)).strftime("%F %T")
  request_reason  = ["attrition", "broken", "user error", "unknown", "out of date", "planned maintenance"].sample

  confirmed_down_time  = Time.at(randtime + randnum.rand(600..1800)).strftime("%F %T")
  confirmed_up_time  = Time.at(randtime + randnum.rand(102000..208000)).strftime("%F %T")

  case_type     = randnum.rand(1..5)
  case_sub_type = randnum.rand(1..5)
  case_priority = randnum.rand(1..4)
  is_internal   = [true, false].sample

  current_person_id = randnum.rand(1..10)
  current_step      = randnum.rand(1..6)
  is_closed         = [true, false].sample

  total_man_hour    = randnum.rand(1..24)
  total_price       = randnum.rand(100.0..500.0).round(2)


  sql = "insert into #{table} (site_id, hospital_id, asset_id, name, asset_name, creator_id, creator_name, create_time,
        requestor_id, requestor_name, request_time, request_reason, case_type, case_sub_type,
        case_priority, is_internal, current_person_id, current_step_id,
        is_closed, total_man_hour, total_price, confirmed_down_time, confirmed_up_time) values (\'#{site_id}\', \'#{hospital_id}\', \'#{asset_id}\',
        \'#{name}\', \'#{asset_name}\', \'#{creator_id}\',\'#{creator_name}\', \'#{create_time}\',
        \'#{requestor_id}\', \'#{requestor_name}\',\'#{request_time}\', \'#{request_reason}\',
        \'#{case_type}\', \'#{case_sub_type}\',\'#{case_priority}\', \'#{is_internal}\',
        \'#{current_person_id}\', \'#{current_step}\', \'#{is_closed}\', \'#{total_man_hour}\', \'#{total_price}\', \'#{confirmed_down_time}\', \'#{confirmed_up_time}\')"
  puts sql
  conn.exec(sql)
end

table = "pm_order"
totalrecords = 500
for i in 0..totalrecords
  site_id         = randnum.rand(1..2)
  hospital_id     = [1, 2, 2, 3].sample
  asset_id        = randnum.rand(1..asset_accounts)
  asset_name      = "asset-#{asset_id}"
  name            = "pm-order-#{asset_id}"
  creator_id    = randnum.rand(1..10)
  creator_name  = "creator-#{creator_id}"
  owner_id    = randnum.rand(1..5)
  owner_name  = "owner-#{owner_id}"

  randtime = (duration * rand).to_i + stime.to_i
  create_time = Time.at(randtime).strftime("%F %T")
  start_time = create_time

  is_finished   = [true, false].sample

  sql = "insert into #{table} (site_id, hospital_id, asset_id, name, asset_name, creator_id, creator_name, owner_id, owner_name, create_time, start_time,
        is_finished) values (\'#{site_id}\', \'#{hospital_id}\', \'#{asset_id}\',
        \'#{name}\', \'#{asset_name}\', \'#{creator_id}\',\'#{creator_name}\', \'#{owner_id}\',\'#{owner_name}\', \'#{create_time}\', \'#{start_time}\',
        \'#{is_finished}\')"
  puts sql
  conn.exec(sql)
end
#
#
table = "work_order_step"
totalrecords = 10000
for i in 0..totalrecords
  site_id         = randnum.rand(1..2)
  work_order_id   = randnum.rand(1..400)
  step_id = randnum.rand(1..6)
  step_name       = ["申请", "审核", "派工", "领工", "维修","关单"][step_id - 1]
  owner_id    = randnum.rand(1..5)
  owner_name  = "owner-#{owner_id}"

  randtime = (duration * rand).to_i + stime.to_i
  start_time = Time.at(randtime).strftime("%F %T")
  durations = [600, 1900, 2600, 3700, 6000, 12000, 30000, 60000, 390000, nil]
  tmp = durations[work_order_id % durations.size]

  if tmp == nil
    sql = "insert into #{table} (site_id, work_order_id, step_id, step_name, owner_id, owner_name, start_time)
          values (\'#{site_id}\', \'#{work_order_id}\', \'#{step_id}\',
          \'#{step_name}\', \'#{owner_id}\',\'#{owner_name}\', \'#{start_time}\')"
  else
    tmp = tmp / (totalrecords / 200)
    end_time = Time.at(randtime + tmp * rand2.rand * 2).strftime("%F %T")
    sql = "insert into #{table} (site_id, work_order_id, step_id, step_name, owner_id, owner_name, start_time,
          end_time) values (\'#{site_id}\', \'#{work_order_id}\', \'#{step_id}\',
          \'#{step_name}\', \'#{owner_id}\',\'#{owner_name}\', \'#{start_time}\',
          \'#{end_time}\')"
  end

  puts sql
  conn.exec(sql)
end


table = "asset_clinical_record"
totalrecords = 100000
for i in 1..totalrecords
  site_id = randnum.rand(1..2)
  hospital_id = [1, 2, 2, 3].sample
  asset_id = randnum.rand(1..asset_accounts)

  modality_id       = randnum.rand(1..50)
  modality_type_id  = randnum.rand(1..10)
  modality_type     = type.sample
  procedure_id      = randnum.rand(1..5)
  procedure_name    = ["头部", "胸部", "腹部", "四肢", "其他"][procedure_id - 1]
  procedure_step_id = randnum.rand(1..6)
  procedure_step_name = ["Step One", "Step Two", "Step Three", "Step Four", "Step Five"].sample

  price_amount = randnum.rand(50..400) * rand2.rand * 2
  inject_count = randnum.rand(600.0..1800.0).round(2)
  expose_count = randnum.rand(200.0..400.0).round(2)
  film_count   = randnum.rand(1..6)

  randtime = (duration * rand).to_i + stime.to_i
  puts Time.at(randtime).to_s
  exam_date = Time.at(randtime).strftime("%F")
  exam_stime = Time.at(randtime).strftime("%F %T")
  exam_duration = randnum.rand(1800..36000)

  sql = "insert into #{table} (site_id, hospital_id, asset_id, price_amount, inject_count, expose_count, film_count,
        exam_date, exam_start_time, exam_duration, modality_id, modality_type_id, modality_type,
        procedure_id, procedure_name, procedure_step_id, procedure_step_name) values (\'#{site_id}\', \'#{hospital_id}\', \'#{asset_id}\',
        \'#{price_amount}\', \'#{inject_count}\',\'#{expose_count}\', \'#{film_count}\',
        \'#{exam_date}\', \'#{exam_stime}\', \'#{exam_duration}\', \'#{modality_id}\', \'#{modality_type_id}\', \'#{modality_type}\',
        \'#{procedure_id}\', \'#{procedure_name}\', \'#{procedure_step_id}\',\'#{procedure_step_name}\')"
  puts sql
  conn.exec(sql)
end


#
# # table = "asset_info"
# # res = conn.exec("select id, purchase_price, lifecycle from #{table} where purchase_price is null")
# # res.each do |item|
# #   conn.exec("update #{table} set purchase_price = \'#{randnum.rand(4000..8000)}\',
# #             lifecycle = \'#{randnum.rand(3..5)}\' where id = \'#{item[id]}\'")


table = "inspection_order"
totalrecords = 3000
for i in 1..totalrecords
  # TODO: fix "null value in column "hospital_id" violates not-null constraint (PG::NotNullViolation)"
  hospital_id = [1, 2, 2, 3].sample
  order_type = randnum.rand(1..3)
  site_id = randnum.rand(1..2)
  name = "#{["巡检","计量","质控"][order_type - 1]}-#{i}"
  creator_id    = randnum.rand(1..10)
  creator_name  = "creator-#{creator_id}"

  owner_id = randnum.rand(1..5)
  owner_name  = "task-owner-#{owner_id}"
  owner_org_id = randnum.rand(1..5)
  owner_org_name  = "org-#{owner_id}"

  randtime = (duration * rand).to_i + stime.to_i
  create_time = Time.at(randtime).strftime("%F %T")
  start_time = create_time
  end_time = Time.at(randtime + randnum.rand(1200..2400)).strftime("%F %T")
  is_finished = [true, false].sample



  sql = "insert into #{table} (order_type, site_id, name, creator_id, creator_name, create_time, owner_id, owner_name,
         owner_org_id, owner_org_name, start_time, end_time, is_finished, hospital_id) values (\'#{order_type}\', \'#{site_id}\', \'#{name}\',
        \'#{creator_id}\', \'#{creator_name}\',\'#{create_time}\', \'#{owner_id}\',
        \'#{owner_name}\', \'#{owner_org_id}\', \'#{owner_org_name}\', \'#{start_time}\', \'#{end_time}\',
        \'#{is_finished}\', \'#{hospital_id}\')"
  puts sql
  conn.exec(sql)
end


table = "inspection_order_detail"
totalrecords = 9000
for i in 3..totalrecords
  site_id = randnum.rand(1..2)
  order_id = i / 3
  dept_id = [1,2,3,4,4,5].sample
  dept_name = ["超声诊断室","肿瘤中心","心超室","放射科","心导管室"][dept_id - 1]
  asset_id       = randnum.rand(1..asset_accounts)
  asset_name     = "asset-#{asset_id}"
  item_id        = i
  item_name      = "item-#{i}"

  is_passed = [true, true, true, false].sample

  sql = "insert into #{table} (site_id, order_id, dept_id, dept_name, asset_id, asset_name, item_id, item_name, is_passed) values
        (\'#{site_id}\', \'#{order_id}\', \'#{dept_id}\',  \'#{dept_name}\', \'#{asset_id}\',
        \'#{asset_name}\', \'#{item_id}\',\'#{item_name}\', \'#{is_passed}\')"
  puts sql
  conn.exec(sql)
end
