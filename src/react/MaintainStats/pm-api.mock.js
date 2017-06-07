{  
  pages: {  
    total:120,
    limit: function({  _req }) {
      return _req.query.limit || 15
    },
    start: function({  _req }) {
      return _req.query.start || 0
    }
  },
  root: {
    "completion": {
      "incomplete": "@integer(12, 15)",
      "completed": "@integer(50, 100)",
      "all": "@integer(120, 200)"
    },
    "quality": {
      "repair": "@integer(100, 150)",
      "all": "@integer(120, 200)"
    }
  },
  "items|15": [
    {
      "key": {
        "id": "@id()",
        "name": "@city()",// "@province()"
      },
      "val": {
        "completion": {
          "incomplete": "@integer(2, 5)",
          "completed": "@integer(12, 15)",
          "all": "@integer(10, 20)"
        },
        "quality": {
          "repair": "@integer(10, 15)",
          "all": "@integer(10, 20)"
        }
      }
    }
  ]
}
