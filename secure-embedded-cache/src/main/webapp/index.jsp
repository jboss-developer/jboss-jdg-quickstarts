<!DOCTYPE html>
<html lang="en"><head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cache Contents and Operations</title>

    <link rel="stylesheet" media="all" href="css/bootstrap-2.3.2.min.css" />

	<script type="text/javascript" src="js/jquery-1.11.1.min.js"></script>
	
	<script type="text/javascript">

		$(document).ready(function() {
		  $(window).keydown(function(event){
		    if(event.keyCode == 13) {
		      event.preventDefault();
		      return false;
		    }
		  });
		});

		function fetchLoggedUser() {
			$("#loggedUser").html("");
			$.ajax({
				url: 'rest/cache/loggedUser',
				type: 'GET',
				success: function(result) {
					$("#loggedUser").html(result);
				}
			});
		}
	
		function fetchCacheEntries() {

			var rowcount = jQuery("#cacheTable tr").length;
			jQuery.getJSON('rest/cache/get', null, function(data) {
				if (data) {
					if (data.failed) {
						alert("Operation Failed :\n"+data.failureMessage);
						return;
					} 
					var cacheEntries = data.outputEntries;
					if (cacheEntries.length == 0) {
						$("#cacheTable > tbody").html("");
					} else if (cacheEntries.length != (rowcount -1) && cacheEntries.length !=0) {
						$("#cacheTable > tbody").html("");
						var $cacheTable = jQuery("#cacheTable tbody");
						for (var i in cacheEntries) {
							kvPair = cacheEntries[i];
							var $row = jQuery("<tr>");
							var $col1 = jQuery("<td style=\"border: 1px solid green;text-align:center\">");
							var $col2 = jQuery("<td style=\"border: 1px solid green;text-align:center\">");
							var $col3 = jQuery("<td style=\"border: 1px solid green;text-align:center\">");
							var img = $('<img/>', { 
								        src: 'img/del.png', 
						               alt:'Delete Key/Value Pair'})
						              .click(function() {
							              remove(this);
							              })
						              .appendTo($col3);
							$col1.html(kvPair.key);
							$col2.html(kvPair.value);
							$row.append($col1);
							$row.append($col2);
							$row.append($col3);
							$cacheTable.append($row);
						}
					}
				}
				setTimeout('fetchCacheEntries()', 2000);
			});
		}

		function remove(img) {
			var $img = jQuery(img);
			var $tdImg = $img.parent();
			var $tdVal = $tdImg.prev();
			var $tdKey = $tdVal.prev(); 
			$.ajax({
			    url: 'rest/cache/remove?key='+$tdKey.html()+'&value='+$tdVal.html(),
			    type: 'DELETE',
			    success: function(result) {
					if (result.failed) {
						alert("Operation Failed :\n"+result.failureMessage);
						return;
					} 			        
			    }
			});
		}
		
		function add() {
			var $key = $("#key");
			var $value = $("#value");
			$.ajax({
			    url: 'rest/cache/put?key='+$key.val()+'&value='+$value.val(),
			    type: 'PUT',
			    success: function(result) {
				    //alert(result);
				    if(result.failed) {
						alert("Operation Failed :\n"+result.failureMessage);
					}
			        $key.val("");
			        $value.val("");      
			    }
			});
		}

		$(document).ready(function() {
			fetchLoggedUser();
			fetchCacheEntries();
		});
	</script>
	
    <style type="text/css">

      body {
        background-color: #eee;
      }
	  
    </style>

  </head>
  <body>
    <div class="container" style="width:700px">
        <h2>Secured Embedded Cache Contents and Operations</h2>
        <div style="-moz-border-radius: 15px;background-color: #E6E6E6;border-radius: 15px;margin:10px 0px 10px 0px;padding: 10px;text-align: center">
          <label id="loggedUser" style="font-weight: bold"></label>
          <label for="key">Provide the ( KEY , VALUE ) pair to be added to Cache</label>
          <input class="form-control" id="key" placeholder="Key" autocomplete="on" type="text">
          <input class="form-control" id="value" placeholder="Value" autocomplete="on" type="text">
		  <br/>
		  <button class="btn btn-lg btn-primary" style="width:445px" onclick="add()">Add</button>
        </div>
        <h3>Cache Contents</h3>
		<table id="cacheTable" style="width:100%; border: 1px solid green; ">
			<thead>
			<tr style="background-color: green; color: white;">
				<th style="width:48%; border: 1px solid green;">Key</th>
				<th style="width:48%; border: 1px solid green;">Value</th>
				<th style="width:4%; border: 1px solid green;">Delete?</th>
			</tr>
			</thead>
			<tbody>
			</tbody>
		</table>
    </div> <!-- /container -->
</body></html>