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
  var props = PropertiesService.getScriptProperties();
  var lock = LockService.getScriptLock();
  var cacheKey = operationId ? ("op_" + operationId) : null;

  function isAlreadyApplied() {
    if (!cacheKey) {
      return false;
    }
    // Cache rápido (TTL 6h) + Properties como respaldo más duradero ante reintentos tardíos
    return cache.get(cacheKey) != null || props.getProperty(cacheKey) != null;
  }

  function rememberApplied() {
    if (!cacheKey) {
      return;
    }
    cache.put(cacheKey, "1", 21600); // 6 horas (máx. CacheService)
    props.setProperty(cacheKey, String(new Date().getTime()));
  }

  function dedupedResponse() {
    return ContentService
      .createTextOutput(JSON.stringify({
        sheet: sheetName,
        previousAmount: 0,
        finalAmount: 0,
        deduped: true
      }))
      .setMimeType(ContentService.MimeType.JSON);
  }

  // Idempotencia rápida sin lock
  if (isAlreadyApplied()) {
    return dedupedResponse();
  }

  lock.waitLock(30000);
  try {
    // Re-check tras adquirir el lock (race entre requests concurrentes)
    if (isAlreadyApplied()) {
      return dedupedResponse();
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

    // Registrar lo antes posible tras el write (lock aún sostenido)
    rememberApplied();

    return textOutput;
  } finally {
    lock.releaseLock();
  }
}
