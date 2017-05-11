{  
  pages:{  
    total:120,
    limit: function({  _req }) {
      return _req.query.limit || 15
    },
    start: function({  _req }) {
      return _req.query.start || 0
    }
  },
  "range":{  
    "man_hour":1921,
    "hour_total":2000,
    "score":5,
    "work_order": 100
  },
  "summary":{  
    "man_hour": "@integer(1000, 1500)",
    "repair": "@integer(100, 300)",
    "maintenance": "@integer(100, 300)",
    "meter": "@integer(100, 300)",
    "inspection": "@integer(100, 300)",
    "work_order": "@integer(500, 1000)",
    "closed": "@integer(100, 600)",
    "open": "@integer(20, 150)",
    "score": "@float(0, 5)",
  },
  "items|15":[  
    {  
      "owner_id": "@id()",
      "owner_name": "@cname()",
      "man_hour": "@integer(100, 150)",
      "repair": "@integer(10, 30)",
      "maintenance": "@integer(10, 30)",
      "meter": "@integer(10, 30)",
      "inspection": "@integer(10, 30)",
      "work_order": "@integer(50, 100)",
      "closed": "@integer(10, 60)",
      "open": "@integer(2, 15)",
      "score": "@float(0, 5)",
    }
  ],
  "link":{  
    "ref":"self",
    "href":"https://www.easy-mock.com/mock/590e9930f926ef14e269a377/api/staff?from=2016-05-11&to=2017-05-11&start=0&limit=15"
  }
}