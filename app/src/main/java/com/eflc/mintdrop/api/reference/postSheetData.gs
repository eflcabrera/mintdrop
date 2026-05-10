function doPost(request) {
  var jsonPayload = JSON.parse(request.postData.contents);

  var spreadsheetId = jsonPayload.spreadsheet_id;
  var sheetName = jsonPayload.sheet;
  var month = jsonPayload.month;
  var amount = String(jsonPayload.amount).replace(".", ",");
  var row = jsonPayload.row;
  var description = jsonPayload.description;
  var isOwedInstallments = jsonPayload.isOwedInstallments;
  var totalInstallments = jsonPayload.totalInstallments;
  var paymentMethod = jsonPayload.paymentMethod;

  var sheet = SpreadsheetApp.openById(spreadsheetId).getSheetByName(sheetName);
  var column = month + 3;
  var textOutput;

  if (isOwedInstallments) {
    for (var counter = 1; counter <= totalInstallments; counter = counter + 1) {
      column = month + 3 + counter;
      var cell = sheet.getRange(row, column);
      var previousAmountNumber = Number(cell.getValue());
      var previousAmount = String(cell.getValue()).replace(".", ",");
      var finalAmountNumber = previousAmountNumber + jsonPayload.amount
      var finalAmount = cell.isBlank() ? `=${amount}` : "=" + previousAmount + `+${amount}`;
      cell.setValue(finalAmount);
      cell.setNote(cell.getNote() + `${cell.getNote().length == 0 ? "":"\n"}${description ? description + " " : ""}${amount} cuota ${counter}/${totalInstallments} con ${paymentMethod}`);

      textOutput = ContentService.createTextOutput(JSON.stringify({ sheet: sheetName, previousAmount: previousAmountNumber, finalAmount: finalAmountNumber })).setMimeType(ContentService.MimeType.JSON);
    }
  } else {
    var cell = sheet.getRange(row, column);
    var previousAmountNumber = Number(cell.getValue());
    var previousAmount = String(cell.getValue()).replace(".", ",");
    var finalAmountNumber = previousAmountNumber + jsonPayload.amount
    var finalAmount = cell.isBlank() ? `=${amount}` : "=" + previousAmount + `+${amount}`;
    cell.setValue(finalAmount);
    cell.setNote(cell.getNote() + `${cell.getNote().length == 0 ? "":"\n"}${description ? description + " " : ""}${amount}`);

    textOutput = ContentService.createTextOutput(JSON.stringify({ sheet: sheetName, previousAmount: previousAmountNumber, finalAmount: finalAmountNumber })).setMimeType(ContentService.MimeType.JSON);
  }

  return textOutput;
}
