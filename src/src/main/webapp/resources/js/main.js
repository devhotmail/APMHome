jQuery(function() {
	
    // HACK to make p:layout work with chromeshades (we just add the !important)
    $(PrimeFaces.escapeClientId('form:top')).css("display","block !important");
    $(PrimeFaces.escapeClientId('form:center')).css("display","block !important");
    $(PrimeFaces.escapeClientId('form:bottom')).css("display","block !important");

	// Add some ARIA role here
    $(PrimeFaces.escapeClientId('form:center')).attr("role","main");
    $(PrimeFaces.escapeClientId('form:bottom')).attr("role","contentinfo");
    $(".aria-role-presentation").attr("role", "presentation");
    $(".aria-search-button").attr("aria-controls", "searchResultsRegion");
    $(".aria-save-button").attr("aria-controls", "messagesRegion");
    
    // Propagate quit action, from previous flow to current flow
    if (document.URL.indexOf('_cascadeQuit', 0) > 0) {
		APP.menu.quit();
	}

    /* temporary fix for keyboard menu navigation */
    $('.ui-menuitem-link').focus(function() {
    	jQuery(this).parent().toggleClass('ui-state-focus');
    	}).blur(function() {
    	jQuery(this).parent().removeClass('ui-state-focus');
    });

    /* default submit button when user press enter */
    $("form input, form select").on("keypress", function (e) {
    	if ($(this).parents("form").find("button[type=submit].default").length <= 0) {
    		return true;
    	}
    	
    	if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
    		$(this).parents("form").find("button[type=submit].default").click();
    		return false;
    	} else {
    		return true;
    	}
    });
    
});

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