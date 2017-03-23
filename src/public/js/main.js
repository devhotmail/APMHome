function handleSubmit(args, dialog) {
    var jqDialog = jQuery('#' + dialog);
    if (args.validationFailed) {
        PF(dialog).show();
    } else {
        PF(dialog).hide();
    }
}

function isValidDateString(dateString) {
    try {
        var dateValue = new Date(dateString);
        if (Object.prototype.toString.call(dateValue) !== "[object Date]")
            return false;

        return !isNaN(dateValue.getTime());
    } catch (e) {
    }
    ;
    return false;
}

function filterByDateFieldOnBlur(obj, delimiter) {
    var strDate = obj.value;
    strDate = strDate.replace(/_/g, "0");
    var dates = strDate.split(delimiter);

    if (!isValidDateString(dates[0]))
        dates[0] = "0000-00-00";
    if (!isValidDateString(dates[1]))
        dates[1] = "0000-00-00";

    if ((dates[0] === "0000-00-00") && (dates[0] === "0000-00-00")) {
        obj.value = "";
        obj.style.borderColor = "silver";
    } else
        obj.value = dates[0] + delimiter + dates[1];
}

function filterByDateField(e, obj, dataTable, delimiter) {
    if ((e.keyCode !== 8) && (e.keyCode < 46) && (e.keyCode < 57))
        return;

    var strDate = obj.value;
    //strDate = strDate.replace(/_/g, "0");
    var dates = strDate.split(delimiter);
    var isDate1 = isValidDateString(dates[0]);
    var isDate2 = isValidDateString(dates[1]);

    if (isDate1 || isDate2) {
        if (obj.style.borderColor === "silver") {
            if (isDate1 && isDate2)
                PF(dataTable).filter();
        } else {
            obj.style.borderColor = "silver";
            PF(dataTable).filter();
        }
    } else {
        if (obj.style.borderColor === "silver") {
            PF(dataTable).filter();
        }
        var s = "0000-00-00" + delimiter + "0000-00-00";
        if (strDate !== s)
            obj.style.borderColor = "red";
    }
}
function dateToString(aDate) {
    var year = aDate.getFullYear();
    var month = (aDate.getMonth() + 1).toString();
    var day = (aDate.getDate()).toString();
    if (month.length === 1) {
        month = "0" + month;
    }
    if (day.length === 1) {
        day = "0" + day;
    }
    return year + "/" + month + "/" + day;
}
function setDateFilter(dataTableWidgetVar, inputWidgetVar, fromDateWidgetVar, toDateWidgetVar) {
    var input = document.getElementById(PF(inputWidgetVar).id);
    var oldValue = input.value;
    
    var dateFrom = PF(fromDateWidgetVar).getDate();
    var dateTo = PF(toDateWidgetVar).getDate();

    var value = "";
    if (dateFrom !== null)
        value = dateToString(dateFrom);
    if (dateTo !== null)
        value = value + "~" + dateToString(dateTo);

    if(oldValue===value) return;
    
    input.value = value;
    PF(dataTableWidgetVar).filter();
}

function getCurrentDateFilter(inputWidgetVar, displayWidgetVar) {
    var input = document.getElementById(PF(inputWidgetVar).id);
    var display = document.getElementById(PF(displayWidgetVar).id);
    
    if(input.value===null)
        display.value = "N/A";
    else
        display.value = input.value;
    
}