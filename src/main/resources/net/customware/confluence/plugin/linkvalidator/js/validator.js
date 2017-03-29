//////////////// Start of presence check ///////////////
if(typeof LinkValidator=='undefined') {

var LinkValidator = {
	contextPath: "",
	
	validators: new Array(),
	currentValidator: 0,
	
	addValidator: function(validatorElement)
	{
		LinkValidator.validators[LinkValidator.validators.length] = validatorElement;
	},
	
	processNextValidator: function()
	{
		if (LinkValidator.currentValidator == LinkValidator.validators.length)
			return;
			
		var validator = LinkValidator.validators[LinkValidator.currentValidator++];
		
		var params = {
			url: validator.getAttribute("url"),
			verbose: validator.getAttribute("verbose"),
			timeout: validator.getAttribute("timeout")
		};
		
		LinkValidator.sendRequest("/plugins/linkvalidator/validate.action", params, validator);
	},
	
    sendRequest : function(uri, params, statusElement)
    {
        if (uri.charAt(0) == "/")
            uri = LinkValidator.contextPath + uri;

        var sparams = this.serializeParams(params);

        var callback =
        {
            method : "post",

            parameters : sparams,

            onComplete: function(data)
            {
                alert("sendRequest: response received");
                //debug("sendRequest: response received");
                if (data.status == 200)
                {
                    statusElement.innerHTML = data.responseText;
//                    Scaffold.View.handleResponse(data.responseXML);
                }
                else
                {
                    LinkValidator.showAjaxError(statusElement, data);
                }
                
                LinkValidator.processNextValidator();
            },

            onFailure : function(data, object)
            {
                //debug("sendRequest: failure");

                LinkValidator.showError(statusElement, "The operation failed.");

                LinkValidator.processNextValidator();
            },

            onException : function(data, exception)
            {
                //debug("sendRequest: exception");

                LinkValidator.showError(statusElement, "The operation failed: " + exception.getMessage());

                LinkValidator.processNextValidator();
            }
        }

        //debug("sendRequest: requesting '" + uri + "?" + sparams + "'");
        LinkValidator.showWorking(statusElement);

        // Send request to server
        //var req = new Ajax.Request(uri, callback);

        jQuery.ajax({
           url: uri,
           data: sparams
        }).complete(
            function(data){
                if (data.status == 200)
                {
                    statusElement.innerHTML = data.responseText;
                }
                else
                {
                    LinkValidator.showAjaxError(statusElement, data);
                }

                LinkValidator.processNextValidator();
            }
        );
        return false;
    },

	/**
	 * @param uri The main body of the URI.
	 * @param params The set of parameters to attach to the URI.
	 */
    serializeParams : function(params)
    {
        var key, value;
        var first = true;
        var uri = "";

        for (key in params)
        {
            var type = typeof(params[key]);
            if (params[key] != null && type != "function" && type != "undefined")
            {
                if (first)
                    first = false
                else
                    uri += "&";
                value = params[key];

                //debug("buildUri: key = '" + key + "'; value = '" + value + "'; type = " + typeof(params[key]));

                uri += encodeURIComponent(key) + "=" + encodeURIComponent(value);
            }
        }

        return uri;
    },
    

    showAjaxError : function(statusElement, data)
    {
        var message = "An error occurred while processing. Please contact an administrator.";
        switch (data.status)
            {
            case 500:
                message = "There was an error on the server. Please check your server logs for details.";
                break;
            case 404:
                message = "The URL for the requested action does not exist.";
                break;
        }
        LinkValidator.showError(statusElement, message);
    },
    
    showError : function(statusElement, message)
    {
    	LinkValidator.showIcon(statusElement, LinkValidator.getIconPath("emoticons/error.gif"), message);
    },
    
    showWorking : function(statusElement)
    {
    	LinkValidator.showIcon(statusElement, LinkValidator.getIconPath("wait.gif"), "Working...");
    },

    showIcon : function(statusElement, src, message)
    {
        var icon = "<img src='" + src + "' text='" + message + "'/>";
        if (statusElement.getAttribute("verbose") == "true")
        	icon += " " + message;
        statusElement.innerHTML = icon;
    },
    
    getIconPath : function(iconName)
    {
    	return LinkValidator.contextPath + "/images/icons/" + iconName;
    },
    
    rules : {
        '.validatorElement' : function(el)
        {
			LinkValidator.addValidator(el);
        }
    }
};

Behaviour.register(LinkValidator.rules);

Behaviour.addLoadEvent(LinkValidator.processNextValidator);
if (navigator.appName == "Microsoft Internet Explorer") {
Behaviour.start();
}
//////////// End of presence check /////////////
}