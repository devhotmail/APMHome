import {
  RandomInt
} from 'utils/helpers'

export const briefsByType = {
  "data": [{
      "id": "6",
      "name": "DSA",
      "respond": RandomInt(2600000, 3600000),
      "arrived": RandomInt(3600000, 4600000),
      "ETTR": RandomInt(4600000, 9600000)
    },
    {
      "id": "11",
      "name": "PET-MR",
      "respond": RandomInt(2600000, 3600000),
      "arrived": RandomInt(3600000, 4600000),
      "ETTR": RandomInt(4600000, 9600000)
    },
    {
      "id": "4",
      "name": "CR",
      "respond": RandomInt(2600000, 3600000),
      "arrived": RandomInt(3600000, 4600000),
      "ETTR": RandomInt(4600000, 9600000)
    },
    {
      "id": "5",
      "name": "RF",
      "respond": RandomInt(2600000, 3600000),
      "arrived": RandomInt(3600000, 4600000),
      "ETTR": RandomInt(4600000, 9600000)
    },
    {
      "id": "1",
      "name": "CT",
      "respond": RandomInt(2600000, 3600000),
      "arrived": RandomInt(3600000, 4600000),
      "ETTR": RandomInt(4600000, 9600000)
    },
    {
      "id": "2",
      "name": "MR",
      "respond": RandomInt(2600000, 3600000),
      "arrived": RandomInt(3600000, 4600000),
      "ETTR": RandomInt(4600000, 9600000)
    }
  ],
  "page": {
    "total": "6",
    "start": "0",
    "limit": "6"
  }
}

export const briefsBySupplier = {
  "data": [{
      "id": "1",
      "name": "通用电气",
      "respond": "1644",
      "arrived": "2835",
      "ETTR": "6238"
    },
    {
      "id": "2",
      "name": "西门子",
      "respond": "343",
      "arrived": "576",
      "ETTR": "1148"
    },
    {
      "id": "4",
      "name": "霍尼韦尔",
      "respond": "7330",
      "arrived": "10587",
      "ETTR": "16242"
    },
    {
      "id": "3",
      "name": "飞利浦",
      "respond": "2518",
      "arrived": "4126",
      "ETTR": "7184"
    }
  ],
  "page": {
    "total": "4",
    "start": "0",
    "limit": "4"
  }
}

export const briefsByDept = {
  "data": [{
      "id": "2",
      "name": "肿瘤中心",
      "respond": "2589",
      "arrived": "5252",
      "ETTR": "10253"
    },
    {
      "id": "3",
      "name": "心超室",
      "respond": "1266",
      "arrived": "1674",
      "ETTR": "2688"
    },
    {
      "id": "27",
      "name": "科室1",
      "respond": "16278",
      "arrived": "24610",
      "ETTR": "31510"
    },
    {
      "id": "5",
      "name": "心导管室",
      "respond": "458",
      "arrived": "741",
      "ETTR": "1300"
    },
    {
      "id": "4",
      "name": "放射科",
      "respond": "4327",
      "arrived": "6218",
      "ETTR": "10204"
    }
  ],
  "page": {
    "total": "5",
    "start": "0",
    "limit": "5"
  }
}
