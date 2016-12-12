require "pg"

def get_services
  if ENV['VCAP_SERVICES']
    @services = JSON.parse(ENV['VCAP_SERVICES'])
  else
    @services = ''
  end

  puts @services
end

def get_pg
  get_services
  if @services == ''
    @pg_host      = "127.0.0.1"
    @pg_database  = "ge_apm"
    @pg_username  = "postgres"
    @pg_password  = "root"
  else
    @pg = @services['postgres'].first
    puts @pg

    @pg_host      = @pg['credentials']['host']
    @pg_database  = @pg['credentials']['database']
    @pg_username  = @pg['credentials']['username']
    @pg_password  = @pg['credentials']['password']
  end

end

def get_conn
  get_pg
  @conn = PG::Connection.new(:hostaddr => @pg_host, :dbname => @pg_database,
                              :user => @pg_username, :password => @pg_password)
  puts @conn.inspect
  @conn
end

# init @conn
get_conn

text = File.open('create_tables.sql').read
@conn.exec(text)

text = File.open('test_data.sql').read
text.gsub!(/\r\n?/, "\n")
text.each_line do |line|
  if line != "\n"
    @conn.exec(line)
  end
end
