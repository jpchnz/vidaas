var svg;

// Relation co-ordinates
var lineX1, lineY1, lineX2, lineY2;

var lines$ = [];
var lineCounter = 0;
var databaseObject; // = new Database();

var tempRelationStart;
var tempRelationEnd;

// Table1 with Primary key
// Table2 with Foreign/primary Key in the relation
var tempTable1;
var tempTable2; 

var oneToOneRelationSelected = false;
var oneToOneNonRelationSelected = false;

var oneToManyRelationSelected = false;
var oneToManyNonRelationSelected = false;

var manyToManyRelationSelected = false;
var manyToManyNonRelationSelected = false;

var columnDragSelected = false;

// Table to receive column
var columnDragSelectedTable;

var xPosition;
var yPosition;
var counter = 0;

$(function() {
	// First Attach SVG
	$('#droppable').svg();

	// After Attaching Retrieve SVG as a varaible
	svg = $('#droppable').svg('get');// svg({onLoad: drawInitial});

	$("img").tooltip();
	$('#droppable').jScrollPane();

	$("#oneToOne").click(
			function() {
				if (oneToOneNonRelationSelected == true
						|| oneToManyRelationSelected == true
						|| oneToManyNonRelationSelected == true
						|| manyToManyRelationSelected == true
						|| manyToManyNonRelationSelected == true) {
					resetRelationsButtons();
				}
				oneToOneRelationSelected = !oneToOneRelationSelected;
				if (oneToOneRelationSelected == true) {
					//
					$("#oneToOneImg").attr("src", "images/1-2-1-Pressed1.png");
				} else {
					$("#oneToOneImg").attr("src", "images/1-2-1-Original.png");
					counter = 0;
				}
			});

	$("#oneToOneNon").click(
			function() {
				if (oneToOneRelationSelected == true
						|| oneToManyRelationSelected == true
						|| oneToManyNonRelationSelected == true
						|| manyToManyRelationSelected == true
						|| manyToManyNonRelationSelected == true) {
					resetRelationsButtons();
				}
				oneToOneNonRelationSelected = !oneToOneNonRelationSelected;
				if (oneToOneNonRelationSelected == true) {
					$("#oneToOneNonImg").attr("src", "images/1-2-1-Pressed-Non1.png");
				} else {
					$("#oneToOneNonImg").attr("src", "images/1-2-1-Original-Non.png");
					counter = 0;
				}
			});

	$("#oneToMany").click(
			function() {
				if (oneToOneRelationSelected == true
						|| oneToOneNonRelationSelected == true
						|| oneToManyNonRelationSelected == true
						|| manyToManyRelationSelected == true
						|| manyToManyNonRelationSelected == true) {
					resetRelationsButtons();
				}
				oneToManyRelationSelected = !oneToManyRelationSelected;
				if (oneToManyRelationSelected == true) {
					$("#oneToManyImg").attr("src", "images/1-2-M-Pressed1.png");
				} else {
					$("#oneToManyImg").attr("src", "images/1-2-M-Original.png");
					counter = 0;
				}
			});

	$("#oneToManyNon").click(
			function() {
				if (oneToOneRelationSelected == true
						|| oneToOneNonRelationSelected == true
						|| oneToManyRelationSelected == true
						|| manyToManyRelationSelected == true
						|| manyToManyNonRelationSelected == true) {
					resetRelationsButtons();
				}
				oneToManyNonRelationSelected = !oneToManyNonRelationSelected;
				if (oneToManyNonRelationSelected == true) {
					$("#oneToManyNonImg").attr("src", "images/1-2-M-Pressed-Non1.png");
				} else {
					$("#oneToManyNonImg").attr("src", "images/1-2-M-Original-Non.png");
					counter = 0;
				}
			});

	$("#manyToMany").click(
			function() {
				if (oneToOneRelationSelected == true
						|| oneToOneNonRelationSelected == true
						|| oneToManyRelationSelected == true
						|| oneToManyNonRelationSelected == true
						|| manyToManyNonRelationSelected == true) {
					resetRelationsButtons();
				}
				manyToManyRelationSelected = !manyToManyRelationSelected;
				if (manyToManyRelationSelected == true) {
					$("#manyToManyImg").attr("src", "images/M-2-M-Pressed1.png");
				} else {
					$("#manyToManyImg").attr("src", "images/M-2-M-Original.png");
					counter = 0;
				}
			});

	$("#manyToManyNon").click(
			function() {
				if (oneToOneRelationSelected == true
						|| oneToOneNonRelationSelected == true
						|| oneToManyRelationSelected == true
						|| oneToManyNonRelationSelected == true
						|| manyToManyRelationSelected == true) {
					resetRelationsButtons();
				}
				manyToManyNonRelationSelected = !manyToManyNonRelationSelected;
				if (manyToManyNonRelationSelected == true) {
					$("#manyToManyNonImg").attr("src", "images/M-2-M-Pressed-Non1.png");
				} else {
					$("#manyToManyNonImg").attr("src", "images/M-2-M-Original-Non.png");
					counter = 0;
				}
			});

	$("#columnType").change(function() {
		alert("select changed");
	});

	$("#tableDrag").draggable(
			{
				distance : 20,
				addClasses : false,
				helper : 'clone',
				opacity : 0.7,
				start : function() {
					$("body").find("#testP").html("Drag Started!");

				},
				drag : function() {
					$("body").find("#testP").html("Dragging!");
				},
				stop : function(event, ui) {/**/
					if (databaseObject == null) {
						$("#externalHtml").load('dialog/databaseForm.html').dialog(
								{
									title : "Please, Create Database First",
									modal : true,
									autoOpen : true,
									height : 325,
									width : 300,
									draggable : false,
									resizable : false,
									close: function(event, ui) { 
										//$("#columnForm").validationEngine('detach'); 
										$('#databaseForm').validationEngine('hide');
									},
									buttons : {
										"Create Database" : function() {

											if ($("#databaseForm").validationEngine('validateField',
													"#name")) {

											} else {

												// var tempTable =
												// new Table();
												// Retrieve the
												// value entered by
												// User in the form
												var databaseName = $('[name=databaseName]').val();
												var descriptionDescription = $(
														'[name=databaseDescription]').val();

												databaseObject = new Database();
												databaseObject.name = databaseName;
												databaseObject.description = descriptionDescription;
												/*
												 * createDatabaseFunction(databaseName,
												 * descriptionDescription);
												 */
												// $(this).dialog("close");
												if (databaseObject != null) {
													$("#externalHtml").load(
															'dialog/databaseConfirmation.html').dialog({
														title : "Database Created",
														modal : true,
														autoOpen : true,
														height : 140,
														width : 305,
														draggable : false,
														resizable : false,
														buttons : {
															"OK" : function() {
																$(this).dialog("close");
															}
														}
													});
												}
											}
										}
									}
								});
					} else {
						console.log("Before creating table: "
								+ databaseObject.tables.length);
						xPosition = ui.position.left;
						yPosition = ui.position.top;
						$("#externalHtml").load('dialog/tableForm.html').dialog(
								{
									title : "Create Table",
									modal : true,
									autoOpen : true,
									height : 340,
									width : 315,
									draggable : false,
									resizable : false,
									close: function(event, ui) { 
										//$("#columnForm").validationEngine('detach'); 
										$('#tableForm').validationEngine('hide');
									},
									buttons : {
										"Create Table" : function() {
											var tempTable = new Table();

											if ($("#tableForm").validationEngine('validateField',
													"#name")) {

											} else {
												// Retrieve the
												// value entered by
												// User in the form
												var tableName = $('[name=tableName]').val();
												var tableDescription = $('[name=tableDescription]').val();
												var tempInnerHTML = tempTable.createAndAddFunction(
														xPosition, yPosition, tableName, tableDescription,
														tempTable, databaseObject);
												$(tempInnerHTML).appendTo($("#droppable"));
												$(this).dialog("close");

												tempTable.height = $("#" + tempTable.id).height();
												tempTable.width = $("#" + tempTable.id).width();
												console.log("After creating table: "
														+ databaseObject.tables.length);

											}
										}
									}
								});
					}

				}
			});

	$("#columnDrag").draggable(
			{
				helper : 'clone',
				opacity : 0.7,
				start : function() {
					columnDragSelected = true;
				},
				drag : function() {
				},
				stop : function(event, ui) {

					// Adding 20 pixels in X and Y to Make it in centre
					findTableForColumnDrag(ui.position.left + 20, ui.position.top + 20);
					if (columnDragSelectedTable != null) {
						var tableToUpdate = "#" + columnDragSelectedTable.id;

						// alert(columnDragSelectedTable.id + " ");
						/**/
						xPosition = ui.position.left;
						yPosition = ui.position.top;
						$("#externalHtml").load('dialog/columnForm.html')
								.dialog(
										{
											title : "Create Column",
											modal : true,
											autoOpen : true,
											height : 548,
											width : 305,
											draggable : false,
											resizable : false,
											close: function(event, ui) { 
												//$("#columnForm").validationEngine('detach'); 
												$('#columnForm').validationEngine('hide');
											},
											buttons : {
												"Create Column" : function() {
													$('[name=tableNameForColumn]').val(columnDragSelectedTable.name);
													
													if ($("#columnForm").validationEngine('validateField',
															"#name")) {

													} else {

														// Retrieve the
														// value entered by
														// User in the form

														var columnKeyType = $(
																"#columnPrimaryKey option:selected").val();
														// alert(columnKeyType);

														var columnName = $('[name=columnName]').val();
														var columnType = $("#columnType option:selected")
																.val();

														var columnLength = 0;
														var columnScale = 0;

														if (columnType == "float4"
																|| columnType == "float8"
																|| columnType == "varchar"
																|| columnType == "char") {
															columnLength = $('[name=columnLength]').val();
															if (columnType == "float4"
																	|| columnType == "float8") {
																columnScale = $('[name=scaleLength]').val();
															}
														}
														// alert(columnLength
														// + " " +
														// columnScale);

														var columnNull = $(
																"#columnOptional option:selected").val();
														var columnUnique = $(
																"#columnUnique option:selected").val();
														
														var columnDescription = $('[name="columnDescription"]').val();
														//alert("columnDescription: " + columnDescription);

														// Primary Here only
														// for Test
														var innerHTML = columnDragSelectedTable
																.updateFunction(columnKeyType, columnName,
																		columnType, columnLength, columnScale,
																		columnNull, columnUnique, false, columnDescription);

														$(tableToUpdate).replaceWith(innerHTML);
														$(this).dialog("close");
													}
												}
											}
										});
					}
					columnDragSelected = false;
				}
			});

	$("th img.drag-icon").live('onmouseover', function(event) {
		this.src = "images/drag2.jpg";
	});

	$(".drag-icon").live('onmouseout', function(event) {
		this.src = "images/drag1.jpg";
	});
	// $( ".tableElement" ).draggable();
	$('.tableElement').live(
			'mousemove',
			function(event) {
				/*
				 * $(this).droppable({ accept:"#columnDrag", drop: function(event, ui) {
				 * alert("Dropped"); }, over: function(event, ui) {
				 * //Core.getConsole().info("OVER"); alert("Over"); }, activate:
				 * function(event, ui) { alert("Activate"); }, out: function(event, ui) {
				 * alert("OUT"); } });
				 */
				$(this).draggable(
						{
							containment : "#droppable",
							handle : "th",
							cursor : "move",
							drag : function() {
								// alert($(this).id +
								// event.currentTarget.id);
								var tempID = event.currentTarget.id;

								var tempTableOnMove = databaseObject
										.findTableInDatabaseByIDFunction(tempID);

								tempTableOnMove.xPosition = $("#" + tempID).offset().left;
								tempTableOnMove.yPosition = $("#" + tempID).offset().top;

								var newXCoordinateForRelation = tempTableOnMove.xPosition
										+ $("#" + tempID).width() / 2;
								var newYCoordinateForRelation = tempTableOnMove.yPosition
										+ $("#" + tempID).height() / 2;
								// alert(tempTableOnMove.relations.length);
								// moveLine(event.pageX,
								// event.pageY);
								for ( var i = 0; i < tempTableOnMove.relations.length; i++) {
									var tempRelationOnMove = tempTableOnMove.relations[i];
									// This method needs to be
									// Changed
									updateRelation(tempRelationOnMove.line,
											tempRelationOnMove.orientation,
											newXCoordinateForRelation, tempTableOnMove.yPosition + 12);
								}
							}
						});

			});

	$("#droppable").droppable({
		// accept: '#draggable2a #tableDrag',
		drop : function(event, ui) {
			// alert();
			/**/
			$(this).addClass("ui-state-highlight");
		}
	});
});
/**/
function tableToggleClick(element) {

	if (oneToOneRelationSelected == false && oneToOneNonRelationSelected == false
			&& oneToManyRelationSelected == false
			&& oneToManyNonRelationSelected == false
			&& manyToManyRelationSelected == false
			&& manyToManyNonRelationSelected == false) {
		// alert(elementID);
		$(element).closest('div.tableElement').find('tbody').toggle(
				'slow',
				function() {
					$(element).closest('div.tableElement th').toggleClass('rolledup',
							$(this).is(':hidden'));
				});
		// $(element).css("background", "silver");
	}
}

function dragIconMouseOver(element) {
	// alert("mouse on icon " + element);
	element.src = "images/drag2.jpg";
}

function dragIconMouseOut(element) {
	// alert("mouse on icon " + element);
	element.src = "images/drag1.jpg";
}

function modifyColumn(columnID, tableID) {
	// alert(columnID + " " + tableID);

	var tempID = "#" + tableID;
	var tempTable = databaseObject.findTableInDatabaseByIDFunction(tableID);

	var tempColumn = findColumnInTable(tempTable, columnID);

	if (tempColumn != null) {
		var tempColumnName = tempColumn.keyName;
		var tempColumnDataType = tempColumn.dataType;
		var tempColumnKeyType = tempColumn.keyType;
		var tempColumnLength = tempColumn.keyLength;
		var tempColumnNull = tempColumn.keyNull;
		var tempColumnUnique = tempColumn.keyUnique;
		var tempColumnDescription = tempColumn.keyDescription;

		// alert(tempColumnNull + " " + tempColumnUnique);

		$("#externalHtml").load('dialog/columnForm.html').dialog(
				{
					title : "Update Column",
					modal : true,
					autoOpen : true,
					height : 548,
					width : 305,
					draggable : false,
					resizable : false,
					buttons : {
						"Update Column" : function() {

							// Retrieve the value entered by User in the
							// form
							var columnKeyType = $("#columnPrimaryKey option:selected").val();
							// alert(columnKeyType);

							var columnName = $('[name=columnName]').val();
							var columnType = $("#columnType option:selected").val();

							var columnLength = 0;
							var columnScale = 0;

							if (columnType == "float4" || columnType == "float8"
									|| columnType == "varchar" || columnType == "char") {
								columnLength = $('[name=columnLength]').val();
								if (columnType == "float4" || columnType == "float8") {
									columnScale = $('[name=scaleLength]').val();
								}
							}

							var columnNull = $("#columnOptional option:selected").val();
							var columnUnique = $("#columnUnique option:selected").val();
							
							var columnDescription = $('[name="columnDescription"]').val();

							// Primary Here only for Test
							var innerHTML = tempTable.updateColumnFunction(tempColumn,
									columnKeyType, columnName, columnType, columnLength,
									columnScale, columnNull, columnUnique, false, columnDescription);

							$(tempID).replaceWith(innerHTML);
							$(this).dialog("close");
						},
						"Delete Column" : function() {
							deleteColumn(tempColumn, tempTable);
							$(this).dialog("close");
						}
					}
				});
		/* */
		setTimeout(function() {
			$('[name=columnName]').val(tempColumn.keyName);
			$('[name=columnDescription]').val(tempColumn.keyDescription);

			if (tempColumnNull == "NULL") {
				$('#columnOptional').attr('selectedIndex', 1);
			} else {
				$('#columnOptional').attr('selectedIndex', 2);
			}

			if (tempColumnUnique == "UNIQUE") {
				$('#columnUnique').attr('selectedIndex', 1);
			} else {
				$('#columnUnique').attr('selectedIndex', 2);
			}

			if (tempColumnKeyType == "Primary") {
				$("#columnPrimaryKey").attr('selectedIndex', 1);

				$('#columnUnique').attr('selectedIndex', 1);
				$('#columnUnique').attr('disabled', 'disabled');

				$('#columnOptional').attr('selectedIndex', 2);
				$('#columnOptional').attr('disabled', 'disabled');
			} else if (tempColumnKeyType == "Foreign") {
				$('#columnUnique').attr('disabled', 'disabled');
				$('#columnOptional').attr('disabled', 'disabled');
				$('#columnType').attr('disabled', 'disabled');
				$("#columnPrimaryKey").attr('disabled', 'disabled');
			} else {
				$("#columnPrimaryKey").attr('selectedIndex', 2);
			}
			parseDataTypeForColumDialog(tempColumnDataType, tempColumnLength);
		}, 100);
	}
}

function parseDataTypeForColumDialog(columnDataType, keyLengthValue) {
	if (columnDataType == "int2" || columnDataType == "smallint") {
		$('#columnType').attr('selectedIndex', 1);
	} else if (columnDataType == "int4" || columnDataType == "integer") {
		$('#columnType').attr('selectedIndex', 2);
		$('[name=columnLength]').val(keyLengthValue);
	} else if (columnDataType == "int8" || columnDataType == "bigint") {
		$('#columnType').attr('selectedIndex', 3);
		$('[name=columnLength]').val(keyLengthValue);
	} else if (columnDataType == "float4") {
		$('#columnType').attr('selectedIndex', 4);
	} else if (columnDataType == "float8") {
		$('#columnType').attr('selectedIndex', 5);
	} else if (columnDataType == "money") {
		$('#columnType').attr('selectedIndex', 6);
	} else if (columnDataType == "char") {
		$('#columnType').attr('selectedIndex', 7);
		$('[name=columnLength]').val(keyLengthValue);
	} else if (columnDataType == "vchar" || columnDataType == "varchar") {
		$('#columnType').attr('selectedIndex', 8);
		$('[name=columnLength]').val(keyLengthValue);
	} else if (columnDataType == "text") {
		$('#columnType').attr('selectedIndex', 9);
	} else if (columnDataType == "bool") {
		$('#columnType').attr('selectedIndex', 10);
	} else if (columnDataType == "date") {
		$('#columnType').attr('selectedIndex', 11);
	}
}

function modifyTable(tableID) {
	var tempID = "#" + tableID;
	var tempTable = databaseObject.findTableInDatabaseByIDFunction(tableID);
	// alert("Time to Modify Table " + tempID);
	$("#externalHtml").load('dialog/tableForm.html').dialog({
		title : "Update Table",
		modal : true,
		autoOpen : true,
		height : 330,
		width : 305,
		draggable : false,
		resizable : false,
		open : function(event, ui) {
			setTimeout(function() {
				$('[name=tableName]').val(tempTable.name);
				$('[name=tableDescription]').val(tempTable.description);
			}, 30);

		},
		buttons : {
			"Update Table" : function() {

				// Retrieve the value entered by User in the form
				var tableName = $('[name=tableName]').val();
				var tableDescription = $('[name=tableDescription]').val();

				tempTable.name = tableName;
				tempTable.description = tableDescription;

				var tempInnerHTML = tempTable.alterFunction(tempTable);

				$(tempID).replaceWith(tempInnerHTML);
				$(this).dialog("close");
			},
			"Delete Table" : function() {
				deleteTable(tempTable, databaseObject);
				// deleteTableFromDatabase(tempTable.id);
				$(this).dialog("close");
			}
		}
	});

	setTimeout(function() {
		$('[name=tableName]').val(tempTable.name);
		$('[name=tableDescription]').val(tempTable.description);
	}, 50);

}

function onMouseClickTable(element) {

	var tempID = "#" + element.id;
	// alert($( tempID).height());
	// alert(element.width + " " + $( "#" +element.id).height() + " " + $( "#"
	// +element.id).width() + " " + element.xPosition + " " + $( "#"
	// +element.id).offset().left);
	/*
	 * if (oneToOneRelationSelected == false && oneToOneNonRelationSelected ==
	 * false && oneToManyRelationSelected == false && oneToManyNonRelationSelected ==
	 * false &&manyToManyRelationSelected == false &&
	 * manyToManyNonRelationSelected == false){ //alert(elementID);
	 * $(element).closest('div.tableElement').find('tbody')
	 * .toggle('slow',function(){ $(element) .toggleClass('rolledup',
	 * $(this).is(':hidden')); });
	 * 
	 * //$(element).css("background", "silver"); } else
	 */
	if (oneToOneRelationSelected == true || oneToOneNonRelationSelected == true
			|| oneToManyRelationSelected == true
			|| oneToManyNonRelationSelected == true) {
		if (counter == 0) {
			// alert(event.pageX);
			lineX1 = $("#" + element.id).offset().left;
			lineY1 = $("#" + element.id).offset().top + 12;
			tempTable1 = databaseObject.findTableInDatabaseByIDFunction(element.id);
			// alert("2: " + tempID);
			counter++;
		} else if (counter > 0) {
			lineX2 = $("#" + element.id).offset().left;
			lineY2 = $("#" + element.id).offset().top + 12;

			tempTable2 = databaseObject.findTableInDatabaseByIDFunction(element.id);
			drawLine();

			resetRelationsButtons();

			/*
			 * counter = 0; oneToOneRelationSelected = false;
			 * //$("#oneToOne").css("background", "silver");
			 * $("#oneToOneImg").attr("src","images/1-2-1-Original.png");
			 */
		}
	} else if (manyToManyRelationSelected == true
			|| manyToManyNonRelationSelected == true) {
		var tempLineX2, tempLineY2;
		var tempTempTable2;
		if (counter == 0) {
			// alert(event.pageX);
			lineX1 = $("#" + element.id).offset().left;
			lineY1 = $("#" + element.id).offset().top + 10;
			tempTable1 = databaseObject.findTableInDatabaseByIDFunction(element.id);
			// alert("2: " + tempID);
			counter++;
		} else if (counter > 0) {
			tempLineX2 = $("#" + element.id).offset().left;
			tempLineY2 = $("#" + element.id).offset().top;

			tempTempTable2 = databaseObject
					.findTableInDatabaseByIDFunction(element.id);
			// drawLine();

			// create New Table
			var tempTable = new Table();
			// Retrieve the value entered by User in the form
			var tableName = tempTable1.name + "_" + tempTempTable2.name;
			lineX2 = (lineX1 + tempLineX2) / 2;
			lineY2 = (lineY1 + tempLineY2) / 2 + 20;
			var tempInnerHTML = tempTable.createAndAddWithoutPKFunction(lineX2,
					lineY2, tableName, tempTable, databaseObject);
			$(tempInnerHTML).appendTo($("#droppable"));

			tempTable.height = $("#" + tempTable.id).height();
			tempTable.width = $("#" + tempTable.id).width();

			tempTable2 = tempTable;
			// alert(tempTable2.id);
			drawLine();

			lineX1 = tempLineX2;
			lineY1 = tempLineY2;
			tempTable1 = tempTempTable2;
			drawLine();

			resetRelationsButtons();
		}
	}
}
// Utility function to draw relations for the onMouseClickTable(element) {}
function initiateRelation(element) {

}

function resetRelationsButtons() {
	counter = 0;

	oneToOneRelationSelected = false;
	oneToOneNonRelationSelected = false;

	oneToManyRelationSelected = false;
	oneToManyNonRelationSelected = false;

	manyToManyRelationSelected = false;
	manyToManyNonRelationSelected = false;

	$("#oneToOneImg").attr("src", "images/1-2-1-Original.png");
	$("#oneToOneNonImg").attr("src", "images/1-2-1-Original-Non.png");

	$("#oneToManyImg").attr("src", "images/1-2-M-Original.png");
	$("#oneToManyNonImg").attr("src", "images/1-2-M-Original-Non.png");

	$("#manyToManyImg").attr("src", "images/M-2-M-Original.png");
	$("#manyToManyNonImg").attr("src", "images/M-2-M-Original-Non.png");
}

/*
 * Draw Lines creates two relations. Not sure if it is best logic
 */
function drawLine() {
	var g = svg.group({
		stroke : 'black',
		'stroke-width' : 2
	});
	var tempLine;
	var sx1, sy1, ex2, ey2;

	var table1Width = $("#" + tempTable1.id).width();
	var table1Height = $("#" + tempTable1.id).height();
	var table2Width = $("#" + tempTable2.id).width();
	var table2Height = $("#" + tempTable2.id).height();

	tempLine = svg.line(g, lineX1 + table1Width / 2, lineY1, lineX2 + table2Width
			/ 2, lineY2, {
		id : 'relation_' + lineCounter
	});

	$(tempLine).bind('mouseover', svgOver).bind('mouseout', svgOut);
	lines$[lineCounter] = tempLine;

	tempRelationStart = new Relation();
	tempRelationEnd = new Relation();
	/* */
	tempRelationStart.otherTable = tempTable2;
	tempRelationStart.line = tempLine;
	tempRelationStart.orientation = "start";
	tempRelationStart.mainTable = tempTable1;
	tempRelationStart.secondryTable = tempTable2;

	tempRelationEnd.otherTable = tempTable1;
	tempRelationEnd.line = tempLine;
	tempRelationEnd.orientation = "end";
	tempRelationEnd.mainTable = tempTable1;
	tempRelationEnd.secondryTable = tempTable2;

	// need loop here to find Primary Keys in tempTable1
	// and creates Foreign Keys in tempTable2
	// and update the tempTable2 ....!
	tempTable1.relations[tempTable1.relations.length] = tempRelationStart;
	tempTable2.relations[tempTable2.relations.length] = tempRelationEnd;

	var tableToUpdate = "#" + tempTable2.id;
	// tableToUpdate.xPosition = lineX2;
	// tableToUpdate.yPosition = lineY2;

	var newKeyInSecondryTable;
	// This loop will be different for Identifying and Non Identifying relations
	for ( var i = 0; i < tempTable1.primaryKeys.length; i++) {
		var innerHTML;
		if (oneToOneNonRelationSelected == true) {
			// keyTypeValue, keyNameValue, dataTypeValue, keyUniqueValue,
			// keyAutoIncrementValue
			innerHTML = tempTable2.updateFunction('Foreign', tempTable1.name + "_"
					+ tempTable1.primaryKeys[i].keyName,
					tempTable1.primaryKeys[i].dataType, true, false);
		} else if (oneToOneRelationSelected == true
				|| manyToManyRelationSelected == true
				|| manyToManyNonRelationSelected == true) {
			// keyTypeValue, keyNameValue, dataTypeValue, keyUniqueValue,
			// keyAutoIncrementValue
			innerHTML = tempTable2.updateFunction('Primary', tempTable1.name + "_"
					+ tempTable1.primaryKeys[i].keyName,
					tempTable1.primaryKeys[i].dataType, true, false);
		} else if (oneToOneNonRelationSelected == true) {
			// keyTypeValue, keyNameValue, dataTypeValue, keyUniqueValue,
			// keyAutoIncrementValue
			innerHTML = tempTable2.updateFunction('Foreign', tempTable1.name + "_"
					+ tempTable1.primaryKeys[i].keyName,
					tempTable1.primaryKeys[i].dataType, false, false);
		} else if (oneToManyRelationSelected == true) {
			// keyTypeValue, keyNameValue, dataTypeValue, keyUniqueValue,
			// keyAutoIncrementValue
			innerHTML = tempTable2.updateFunction('Primary', tempTable1.name + "_"
					+ tempTable1.primaryKeys[i].keyName,
					tempTable1.primaryKeys[i].dataType, false, false);
		}

		newKeyInSecondryTable = findColumnInTableByName(tempTable2, tempTable1.name
				+ "_" + tempTable1.primaryKeys[i].keyName);
		// alert(newKeyInSecondryTable.keyID + " " +
		// newKeyInSecondryTable.keyName);

		tempRelationStart.keysFromMainTable[tempRelationStart.keysFromMainTable.length] = tempTable1.primaryKeys[i];
		tempRelationStart.keysInSecondryTable[tempRelationStart.keysInSecondryTable.length] = newKeyInSecondryTable;

		tempRelationEnd.keysFromMainTable[tempRelationEnd.keysFromMainTable.length] = tempTable1.primaryKeys[i];
		tempRelationEnd.keysInSecondryTable[tempRelationEnd.keysInSecondryTable.length] = newKeyInSecondryTable;

		$(tableToUpdate).replaceWith(innerHTML);
		// var tempForeignKey = new Key();
		// tempInnerHTML = tempInnerHTML + '<tr><td
		// ondblclick="onMouseClickColumn(this)" id="' +
		// this.primaryKeys[i].keyID +'">' + this.primaryKeys[i].keyName +
		// '</td></tr>';
	}
	lineCounter++;
}

function drawRelationFromXMLSchema(mainColumnNameValue,
		secondaryColumnNameValue) {
	var g = svg.group({
		stroke : 'black',
		'stroke-width' : 2
	});
	var tempLine;
	var sx1, sy1, ex2, ey2;

	var table1Width = $("#" + tempTable1.id).width();
	var table1Height = $("#" + tempTable1.id).height();
	var table2Width = $("#" + tempTable2.id).width();
	var table2Height = $("#" + tempTable2.id).height();

	tempLine = svg.line(g, lineX1 + table1Width / 2, lineY1, lineX2 + table2Width
			/ 2, lineY2, {
		id : 'relation_' + lineCounter
	});

	$(tempLine).bind('mouseover', svgOver).bind('mouseout', svgOut);
	lines$[lineCounter] = tempLine;

	tempRelationStart = new Relation();
	tempRelationEnd = new Relation();
	/* */
	tempRelationStart.otherTable = tempTable2;
	tempRelationStart.line = tempLine;
	tempRelationStart.orientation = "start";
	tempRelationStart.mainTable = tempTable1;
	tempRelationStart.secondryTable = tempTable2;

	tempRelationEnd.otherTable = tempTable1;
	tempRelationEnd.line = tempLine;
	tempRelationEnd.orientation = "end";
	tempRelationEnd.mainTable = tempTable1;
	tempRelationEnd.secondryTable = tempTable2;

	// need loop here to find Primary Keys in tempTable1
	// and creates Foreign Keys in tempTable2
	// and update the tempTable2 ....!
	tempTable1.relations[tempTable1.relations.length] = tempRelationStart;
	tempTable2.relations[tempTable2.relations.length] = tempRelationEnd;

	var keyInSecondryTable = findColumnInTableByName(tempTable2,
			secondaryColumnNameValue);
	var keyInMainTable = findColumnInTableByName(tempTable1, mainColumnNameValue);
	// alert(newKeyInSecondryTable.keyID + " " + newKeyInSecondryTable.keyName);

	tempRelationStart.keysFromMainTable[tempRelationStart.keysFromMainTable.length] = keyInMainTable;
	tempRelationStart.keysInSecondryTable[tempRelationStart.keysInSecondryTable.length] = keyInSecondryTable;

	tempRelationEnd.keysFromMainTable[tempRelationEnd.keysFromMainTable.length] = keyInMainTable;
	tempRelationEnd.keysInSecondryTable[tempRelationEnd.keysInSecondryTable.length] = keyInSecondryTable;

	// var tableToUpdate = "#" + tempTable2.id;
	// tableToUpdate.xPosition = lineX2;
	// tableToUpdate.yPosition = lineY2;

	// var newKeyInSecondryTable;
	// This loop will be different for Identifying and Non Identifying relations
	/*
	 * for (var i = 0; i < tempTable1.primaryKeys.length; i++){ var innerHTML;
	 * 
	 * if (oneToOneNonRelationSelected == true) { //keyTypeValue, keyNameValue,
	 * dataTypeValue, keyUniqueValue, keyAutoIncrementValue innerHTML =
	 * tempTable2.updateFunction('Foreign', tempTable1.name + "_"+
	 * tempTable1.primaryKeys[i].keyName, tempTable1.primaryKeys[i].dataType,
	 * true, false); } else if (oneToOneRelationSelected == true ||
	 * manyToManyRelationSelected == true || manyToManyNonRelationSelected ==
	 * true) { //keyTypeValue, keyNameValue, dataTypeValue, keyUniqueValue,
	 * keyAutoIncrementValue innerHTML = tempTable2.updateFunction('Primary',
	 * tempTable1.name + "_"+ tempTable1.primaryKeys[i].keyName,
	 * tempTable1.primaryKeys[i].dataType, true, false); } else if
	 * (oneToOneNonRelationSelected == true) { //keyTypeValue, keyNameValue,
	 * dataTypeValue, keyUniqueValue, keyAutoIncrementValue innerHTML =
	 * tempTable2.updateFunction('Foreign', tempTable1.name + "_"+
	 * tempTable1.primaryKeys[i].keyName, tempTable1.primaryKeys[i].dataType,
	 * false, false); } else if (oneToManyRelationSelected == true) {
	 * //keyTypeValue, keyNameValue, dataTypeValue, keyUniqueValue,
	 * keyAutoIncrementValue innerHTML = tempTable2.updateFunction('Primary',
	 * tempTable1.name + "_"+ tempTable1.primaryKeys[i].keyName,
	 * tempTable1.primaryKeys[i].dataType, false, false); }
	 * 
	 * 
	 * 
	 * 
	 * //$(tableToUpdate).replaceWith(innerHTML); //var tempForeignKey = new
	 * Key(); //tempInnerHTML = tempInnerHTML + '<tr><td ondblclick="onMouseClickColumn(this)" id="' + this.primaryKeys[i].keyID +'">' +
	 * this.primaryKeys[i].keyName + '</td></tr>'; }
	 */
	lineCounter++;
}

// Relation to be updated with PrimaryKey
function updateRelation() {
}

function svgOver() {
	$(this).attr('stroke-width', 4);
	$(this).attr('stroke', 'grey');
	// alert($(this).attr('id'));
	var tempRelation = tablesInRelation($(this).attr('id'));
	var table1Width = $("#" + tempRelation.mainTable.id).width();
	var tempTable = $("#" + tempRelation.mainTable.id);
	$(tempTable).css('background-color', 'red');
	$(tempTable).closest('div.tableElement').find('thead > tr > th').css(
			'background-color', '#FFCCCC').css('color', 'white');
	// $("#"+ tempRelation.mainTable.id).css('border', '40px');
	$("#" + tempRelation.secondryTable.id).css('background-color', 'red');
	$("#" + tempRelation.secondryTable.id).closest('div.tableElement').find(
			'thead > tr > th').css('background-color', '#FFCCCC').css('color',
			'white');
}

function svgOut() {
	$(this).attr('stroke-width', 2);
	$(this).attr('stroke', 'black');
	var tempRelation = tablesInRelation($(this).attr('id'));
	var table1Width = $("#" + tempRelation.mainTable.id).width();
	$("#" + tempRelation.mainTable.id).css('background-color', 'black');
	$("#" + tempRelation.mainTable.id).closest('div.tableElement').find(
			'thead > tr > th').css('background-color', '#D1D1D1').css('color',
			'black');
	// $("#"+ tempRelation.mainTable.id).css('border', '1px');
	$("#" + tempRelation.secondryTable.id).css('background-color', 'black');
	$("#" + tempRelation.secondryTable.id).closest('div.tableElement').find(
			'thead > tr > th').css('background-color', '#D1D1D1').css('color',
			'black');
}

function tablesInRelation(relationID) {
	var tempTable;
	var tempRelation;
	for ( var i = 0; i < databaseObject.tables.length; i++) {
		tempTable = databaseObject.tables[i];
		for ( var j = 0; j < tempTable.relations.length; j++) {
			tempRelation = tempTable.relations[j];
			var tempLine = tempRelation.line;
			if ($(tempLine).attr('id') == relationID) {
				// alert("Relation Found: " + tempTable.id);
				return tempRelation;
			}
		}
	}
	return null;
}
function updateRelation(line, orientationVal, xCoordinate, yCoordinate) {
	if (orientationVal == "start") {
		$(line).animate({
			svgX1 : xCoordinate,
			svgY1 : yCoordinate
		}, 0);
	} else if (orientationVal == "end") {
		$(line).animate({
			svgX2 : xCoordinate,
			svgY2 : yCoordinate
		}, 0);
	}
}

function moveLine(xTemp, yTemp) {
	// var myY = yTemp - $("#svgbasics").top();
	// alert(yTemp + " " + myY);
	for ( var i = 0; i < lines$.length; i++) {
		$(lines$[i]).animate({
			svgX1 : xTemp,
			svgY1 : yTemp
		}, 0);
	}
	// $(tempLine$).remove();
}

// Function to Drag a Column to the Table
// To add new column to the table
function findTableForColumnDrag(xTemp, yTemp) {
	columnDragSelectedTable = null;
	var tempTableInDatabase;
	for ( var i = 0; i < databaseObject.tables.length; i++) {
		tempTableInDatabase = databaseObject.tables[i];
		var startingX = tempTableInDatabase.xPosition;
		var endingX = startingX + tempTableInDatabase.width;

		var startingY = tempTableInDatabase.yPosition;
		var endingY = startingY + tempTableInDatabase.height;

		// alert(xTemp + " " + yTemp + " " + startingX + " " + endingX + " " +
		// startingY + " " + endingY);

		// Subtracting 20 from start i.e. 65 becomes 45
		// and adding 20 to end i.e. 145 becomes 165 to avoid slight error
		if (xTemp > startingX - 20 && xTemp < endingX + 20) {
			if (yTemp > startingY - 20 && yTemp < endingY + 20) {
				// alert (startingX + " " + endingX + " " + startingY + " " +
				// endingY + " ");
				columnDragSelectedTable = tempTableInDatabase;
				// alert(tempTableInDatabase.id);
				// return tempTableInDatabase;
			}
		}
	}
	// return columnDragSelectedTable;
}

function findColumnInTable(parentTable, columnID) {
	for ( var i = 0; i < parentTable.primaryKeys.length; i++) {
		if (parentTable.primaryKeys[i].keyID == columnID) {
			// alert("Primary Key Found");
			return parentTable.primaryKeys[i];
		}
	}

	for ( var i = 0; i < parentTable.columns.length; i++) {
		if (parentTable.columns[i].keyID == columnID) {
			// alert("Column Key Found");
			return parentTable.columns[i];
		}
	}

	for ( var i = 0; i < parentTable.foreignKeys.length; i++) {
		if (parentTable.foreignKeys[i].keyID == columnID) {
			// alert("Foreign Key Found");
			return parentTable.foreignKeys[i];
		}
	}

	return null;
}
/*
function deleteColumnFromTable(parentTable, columnID) {
	for ( var i = 0; i < parentTable.primaryKeys.length; i++) {
		if (parentTable.primaryKeys[i].keyID == columnID) {
			// alert("Primary Key Found");
			parentTable.primaryKeys.splice(i, 1);
		}
	}

	for ( var i = 0; i < parentTable.columns.length; i++) {
		if (parentTable.columns[i].keyID == columnID) {
			alert("Column Key Found");
			parentTable.columns.splice(i, 1);
		}
	}

	for ( var i = 0; i < parentTable.foreignKeys.length; i++) {
		if (parentTable.foreignKeys[i].keyID == columnID) {
			// alert("Foreign Key Found");
			parentTable.foreignKeys.splice(i, 1);
		}
	}
}
*/
function findColumnInTableByName(parentTable, columnName) {
	for ( var i = 0; i < parentTable.primaryKeys.length; i++) {
		if (parentTable.primaryKeys[i].keyName == columnName) {
			// alert("Primary Key Found");
			return parentTable.primaryKeys[i];
		}
	}

	for ( var i = 0; i < parentTable.columns.length; i++) {
		if (parentTable.columns[i].keyName == columnName) {
			// alert("Column Key Found");
			return parentTable.columns[i];
		}
	}

	for ( var i = 0; i < parentTable.foreignKeys.length; i++) {
		if (parentTable.foreignKeys[i].keyName == columnName) {
			// alert("Foreign Key Found");
			return parentTable.foreignKeys[i];
		}
	}

	return null;
}

var tempSQL;
function generateSQL() {

	tempSQL = databaseObject.Postgres();

	$("#externalHtml").load('dialog/SQLDialog.html').dialog({
		title : "Generated SQL",
		modal : true,

		height : 475,
		width : 425,
		draggable : false,
		resizable : false,

		buttons : {
			"OK" : function() {
				$(this).dialog("close");
			}
		}
	});

	setTimeout("sqlDiologHack()", 100);
	return tempSQL;
}

function sqlDiologHack() {
	$('[name=sqlText]').val(tempSQL);
}

function loadSQLWithFileName(fileName) {
	fileToLoad = "./sql/" + fileName;
	alert("DDL saved as: " + fileToLoad);
	$.ajax({
		url : fileToLoad,
		dataType : "xml",
		success : parseXMLSuccess,
		error : parseXMLError
	});
}

function loadSQL() {
	$.ajax({
		url : "./sql/sql_xml_5363_5367.xml",
		dataType : "xml",
		success : parseXMLSuccess,
		error : parseXMLError
	});
}

function parseXMLSuccess(xml) {
	var xmlParser = new XMLParser();
	xmlParser.init(xml);
	databaseObject = xmlParser.getDatabase();

	createRelationForLoadedXMLSchema(xmlParser.foreignKeysArrays);
}

function parseXMLError() {
	alert("XML Loaded Error");
}

function createRelationForLoadedXMLSchema(foreignKeyRelationArray) {
	for ( var i = 0; i < foreignKeyRelationArray.length; i++) {
		tempTable1 = databaseObject
				.findTableInDatabaseByNameFunction(foreignKeyRelationArray[i].mainTable);
		lineX1 = $("#" + tempTable1.id).offset().left;
		lineY1 = $("#" + tempTable1.id).offset().top + 12;

		tempTable2 = databaseObject
				.findTableInDatabaseByNameFunction(foreignKeyRelationArray[i].secondaryTable);
		lineX2 = $("#" + tempTable2.id).offset().left;
		lineY2 = $("#" + tempTable2.id).offset().top + 12;
		drawRelationFromXMLSchema(foreignKeyRelationArray[i].mainColumnName,
				foreignKeyRelationArray[i].secondaryColumnName);

		tempTable1 = null;
		tempTable2 = null;
	}
}