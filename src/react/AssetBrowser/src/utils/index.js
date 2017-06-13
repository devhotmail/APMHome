export function getBlength(str) {
  for (var i = str.length, n = 0; i--;) {
    n += str.charCodeAt(i) > 255 ? 2 : 1;
  }
  return n;
}

export function trimString(str, len, endstr) {
  var len = +len,
    endstr = typeof(endstr) == 'undefined' ? "..." : endstr.toString(),
    endstrBl = getBlength(endstr);

  function n2(a) {
    var n = a / 2 | 0;
    return (n > 0 ? n : 1)
  }//用于二分法查找
  if (!(str + "").length || !len || len <= 0) {
    return "";
  }
  if (len < endstrBl) {
    endstr = "";
    endstrBl = 0;
  }
  var lenS = len - endstrBl,
    _lenS = 0,
    _strl = 0;
  while (_strl <= lenS) {
    var _lenS1 = n2(lenS - _strl),
      addn = getBlength(str.substr(_lenS, _lenS1));
    if (addn == 0) {
      return str;
    }
    _strl += addn
    _lenS += _lenS1
  }
  if (str.length - _lenS > endstrBl || getBlength(str.substring(_lenS - 1)) > endstrBl) {
    return str.substr(0, _lenS - 1) + endstr
  } else {
    return str;
  }
}
