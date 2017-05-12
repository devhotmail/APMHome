
var conf = process.env.NODE_ENV === 'development' ? 
           require('./dev') : require('./prod')
export default conf
