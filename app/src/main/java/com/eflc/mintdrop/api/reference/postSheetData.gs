function doPost(request) {
  var jsonPayload = JSON.parse(request.postData.contents);

  var operationId = jsonPayload.operationId;
  var spreadsheetId = jsonPayload.spreadsheet_id;
  var sheetName = jsonPayload.sheet;
  var month = jsonPayload.month;
  var amount = String(jsonPayload.amount).replace(".", ",");
  var row = jsonPayload.row;
  var description = jsonPayload.description;
  var isOwedInstallments = jsonPayload.isOwedInstallments;
  var totalInstallments = jsonPayload.totalInstallments;
  var paymentMethod = jsonPayload.paymentMethod;

  var cache = CacheService.getScriptCache();
  var lock = LockService.getScriptLock();

  // Idempotencia: mismo operationId no vuelve a modificar la celda (TTL 6h)
  if (operationId) {
    var cacheKey = "op_" + operationId;
    if (cache.get(cacheKey) != null) {
      return ContentService
        .createTextOutput(JSON.stringify({
          sheet: sheetName,
          previousAmount: 0,
          finalAmount: 0,
          deduped: true
        }))
        .setMimeType(ContentService.MimeType.JSON);
    }
  }

  lock.waitLock(30000);
  try {
    // Re-check tras adquirir el lock (race entre requests concurrentes)
    if (operationId) {
      var cacheKeyLocked = "op_" + operationId;
      if (cache.get(cacheKeyLocked) != null) {
        return ContentService
          .createTextOutput(JSON.stringify({
            sheet: sheetName,
            previousAmount: 0,
            finalAmount: 0,
            deduped: true
          }))
          .setMimeType(ContentService.MimeType.JSON);
      }
    }

    var sheet = SpreadsheetApp.openById(spreadsheetId).getSheetByName(sheetName);
    var column = month + 3;
    var textOutput;

    if (isOwedInstallments) {
      for (var counter = 1; counter <= totalInstallments; counter = counter + 1) {
        column = month + 3 + counter;
        var cell = sheet.getRange(row, column);
        var previousAmountNumber = Number(cell.getValue());
        var previousAmount = String(cell.getValue()).replace(".", ",");
        var finalAmountNumber = previousAmountNumber + jsonPayload.amount;
        var finalAmount = cell.isBlank() ? `=${amount}` : "=" + previousAmount + `+${amount}`;
        cell.setValue(finalAmount);
        cell.setNote(cell.getNote() + `${cell.getNote().length == 0 ? "" : "\n"}${description ? description + " " : ""}${amount} cuota ${counter}/${totalInstallments} con ${paymentMethod}`);

        textOutput = ContentService.createTextOutput(JSON.stringify({
          sheet: sheetName,
          previousAmount: previousAmountNumber,
          finalAmount: finalAmountNumber,
          deduped: false
        })).setMimeType(ContentService.MimeType.JSON);
      }
    } else {
      var cellSingle = sheet.getRange(row, column);
      var previousAmountNumberSingle = Number(cellSingle.getValue());
      var previousAmountSingle = String(cellSingle.getValue()).replace(".", ",");
      var finalAmountNumberSingle = previousAmountNumberSingle + jsonPayload.amount;
      var finalAmountSingle = cellSingle.isBlank() ? `=${amount}` : "=" + previousAmountSingle + `+${amount}`;
      cellSingle.setValue(finalAmountSingle);
      cellSingle.setNote(cellSingle.getNote() + `${cellSingle.getNote().length == 0 ? "" : "\n"}${description ? description + " " : ""}${amount}`);

      textOutput = ContentService.createTextOutput(JSON.stringify({
        sheet: sheetName,
        previousAmount: previousAmountNumberSingle,
        finalAmount: finalAmountNumberSingle,
        deduped: false
      })).setMimeType(ContentService.MimeType.JSON);
    }

    if (operationId) {
      cache.put("op_" + operationId, "1", 21600); // 6 horas
    }

    return textOutput;
  } finally {
    lock.releaseLock();
  }
}
