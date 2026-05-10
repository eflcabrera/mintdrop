function getData(spreadsheetId, sheet) {
  var values = Sheets.Spreadsheets.Values.get(spreadsheetId, sheet + '!A:C').values;

  if (!values) {
    return { error: "No data found" }
  }

  var responseJson = [];

  for (var row = 0; row < values.length; row++) {
    var value = values[row][0];
    var categoryId = values[row][1];
    if (value !== "" && value !== undefined && value !== "end" && value !== "Total al mes:") {
      var subCategories = getSubcategories(row + 1, values);
      responseJson.push({ id: categoryId, name: value, subcategories: subCategories });
      // Saltar subcategorías + fila "end" + fila "Total al mes:" + fila vacía
      row += subCategories.length + 3;
    }
  }

  return JSON.stringify({ categories: responseJson });
}

function getSubcategories(row, values) {
  var subcategories = [];

  while (row < values.length && values[row][2] !== "" && values[row][2] !== undefined && values[row][0] !== "end") {
    subcategories.push({ id: values[row][1], name: values[row][2], rowNumber: (row + 1) });
    row++;
  }

  return subcategories;
}

function doGet(request) {
  if (request.parameter.spreadsheetId !== undefined && request.parameter.sheet !== undefined) {
    return ContentService.createTextOutput(getData(request.parameter.spreadsheetId, request.parameter.sheet)).setMimeType(ContentService.MimeType.JSON);
  }

  return ContentService.createTextOutput(JSON.stringify({ error: "Parameter spreadsheetId or sheet not found" }));
}
