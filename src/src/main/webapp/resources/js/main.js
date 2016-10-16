function handleSubmit(args, dialog) {
    var jqDialog = jQuery('#' + dialog);
    if (args.validationFailed) {
        PF(dialog).show();
    } else {
        PF(dialog).hide();
    }
}

function isValidDateString(dateString) {
    try{
        var dateValue = new Date(dateString);
        if ( Object.prototype.toString.call(dateValue) !== "[object Date]" )
          return false;

        return !isNaN(dateValue.getTime());
    }
    catch(e){
    };
    return false;
}

function filterByDateFieldOnBlur(obj, delimiter){
    var strDate = obj.value;
    strDate = strDate.replace(/_/g, "0");
    var dates = strDate.split(delimiter);
    
    if(!isValidDateString(dates[0])) dates[0] = "0000-00-00";
    if(!isValidDateString(dates[1])) dates[1] = "0000-00-00";

    if( (dates[0]==="0000-00-00") && (dates[0]==="0000-00-00") ){
        obj.value = "";
        obj.style.borderColor = "silver";
    }
    else
        obj.value = dates[0] + delimiter + dates[1];
}

function filterByDateField(e, obj, dataTable, delimiter){
    if((e.keyCode!==8) && (e.keyCode<46) && (e.keyCode<57)) return;
    
    var strDate = obj.value;
    //strDate = strDate.replace(/_/g, "0");
    var dates = strDate.split(delimiter);
    var isDate1 = isValidDateString(dates[0]);
    var isDate2 = isValidDateString(dates[1]);
    
    if( isDate1 || isDate2 ){
        if(obj.style.borderColor === "silver"){
            if( isDate1 && isDate2)
                PF(dataTable).filter();
        }
        else{
            obj.style.borderColor = "silver";
            PF(dataTable).filter();
        }
    }
    else{
        if(obj.style.borderColor === "silver"){
            PF(dataTable).filter();
        }
        var s = "0000-00-00" + delimiter + "0000-00-00";
        if(strDate !== s)
            obj.style.borderColor = "red";
    }
}