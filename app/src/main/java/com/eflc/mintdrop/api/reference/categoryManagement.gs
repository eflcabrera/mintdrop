// Script para gestión de categorías y subcategorías en Google Sheets

function createCategory(spreadsheetId, sheetName, categoryName, categoryId) {
  try {
    var sheet = SpreadsheetApp.openById(spreadsheetId).getSheetByName(sheetName);
    
    // Encontrar la última fila con datos
    var lastRow = sheet.getLastRow();
    var insertRow = lastRow + 1;
    
    // Insertar nueva categoría
    sheet.getRange(insertRow, 1).setValue(categoryName);
    sheet.getRange(insertRow, 2).setValue(categoryId);
    
    // Insertar fila "end" para marcar el fin de subcategorías
    sheet.insertRowAfter(insertRow);
    sheet.getRange(insertRow + 1, 1).setValue("end");
    sheet.getRange(insertRow + 1, 2).setValue(""); // ID vacío para fila "end"
    
    // Insertar fila de totales para la categoría
    sheet.insertRowAfter(insertRow + 1);
    sheet.getRange(insertRow + 2, 1).setValue("Total al mes:");
    sheet.getRange(insertRow + 2, 2).setValue(""); // ID vacío para fila de totales
    
    // Insertar fila vacía después de los totales
    sheet.insertRowAfter(insertRow + 2);
    
    return {
      success: true,
      message: "Categoría creada exitosamente",
      rowNumber: insertRow
    };
  } catch (error) {
    return {
      success: false,
      message: "Error al crear categoría: " + error.toString()
    };
  }
}

function createSubcategory(spreadsheetId, sheetName, categoryId, subcategoryName, subcategoryId) {
  try {
    var sheet = SpreadsheetApp.openById(spreadsheetId).getSheetByName(sheetName);
    var values = sheet.getRange(1, 1, sheet.getLastRow(), 3).getValues();
    
    // Encontrar la fila de la categoría
    var categoryRow = -1;
    for (var i = 0; i < values.length; i++) {
      if (values[i][1] === categoryId) {
        categoryRow = i + 1;
        break;
      }
    }
    
    if (categoryRow === -1) {
      return {
        success: false,
        message: "Categoría no encontrada"
      };
    }
    
    // Encontrar la fila "end" de esta categoría
    var endRow = -1;
    for (var i = categoryRow; i < values.length; i++) {
      if (values[i][0] === "end" && values[i][1] === "") {
        endRow = i + 1;
        break;
      } else if (values[i][0] !== "" && values[i][1] !== "" && i > categoryRow) {
        // Nueva categoría encontrada, parar
        break;
      }
    }
    
    if (endRow === -1) {
      return {
        success: false,
        message: "No se encontró la fila 'end' de la categoría"
      };
    }
    
    // Insertar nueva subcategoría ANTES de la fila "end"
    var insertRow = endRow;
    sheet.insertRowBefore(insertRow);
    sheet.getRange(insertRow, 1).setValue(""); // Columna A vacía para subcategorías
    sheet.getRange(insertRow, 2).setValue(subcategoryId);
    sheet.getRange(insertRow, 3).setValue(subcategoryName);
    
    return {
      success: true,
      message: "Subcategoría creada exitosamente",
      rowNumber: insertRow
    };
  } catch (error) {
    return {
      success: false,
      message: "Error al crear subcategoría: " + error.toString()
    };
  }
}

function updateCategory(spreadsheetId, sheetName, categoryId, newName) {
  try {
    var sheet = SpreadsheetApp.openById(spreadsheetId).getSheetByName(sheetName);
    var values = sheet.getRange(1, 1, sheet.getLastRow(), 3).getValues();
    
    // Encontrar la fila de la categoría
    for (var i = 0; i < values.length; i++) {
      if (values[i][1] === categoryId && values[i][0] !== "") {
        sheet.getRange(i + 1, 1).setValue(newName);
        return {
          success: true,
          message: "Categoría actualizada exitosamente"
        };
      }
    }
    
    return {
      success: false,
      message: "Categoría no encontrada"
    };
  } catch (error) {
    return {
      success: false,
      message: "Error al actualizar categoría: " + error.toString()
    };
  }
}

function updateSubcategory(spreadsheetId, sheetName, subcategoryId, newName) {
  try {
    var sheet = SpreadsheetApp.openById(spreadsheetId).getSheetByName(sheetName);
    var values = sheet.getRange(1, 1, sheet.getLastRow(), 3).getValues();
    
    // Encontrar la fila de la subcategoría
    for (var i = 0; i < values.length; i++) {
      if (values[i][1] === subcategoryId && values[i][0] === "" && values[i][2] !== "") {
        sheet.getRange(i + 1, 3).setValue(newName);
        return {
          success: true,
          message: "Subcategoría actualizada exitosamente"
        };
      }
    }
    
    return {
      success: false,
      message: "Subcategoría no encontrada"
    };
  } catch (error) {
    return {
      success: false,
      message: "Error al actualizar subcategoría: " + error.toString()
    };
  }
}

function deleteCategory(spreadsheetId, sheetName, categoryId) {
  try {
    var sheet = SpreadsheetApp.openById(spreadsheetId).getSheetByName(sheetName);
    var values = sheet.getRange(1, 1, sheet.getLastRow(), 3).getValues();
    
    // Encontrar las filas a eliminar
    var categoryStartRow = -1;
    var categoryEndRow = -1;
    
    for (var i = 0; i < values.length; i++) {
      if (values[i][1] === categoryId && values[i][0] !== "") {
        categoryStartRow = i + 1;
        // Encontrar el final de esta categoría (hasta la siguiente categoría o fin de datos)
        for (var j = i + 1; j < values.length; j++) {
          if (values[j][0] !== "" && values[j][1] !== "") {
            categoryEndRow = j;
            break;
          }
        }
        if (categoryEndRow === -1) {
          categoryEndRow = values.length;
        }
        break;
      }
    }
    
    if (categoryStartRow === -1) {
      return {
        success: false,
        message: "Categoría no encontrada"
      };
    }
    
    // Eliminar las filas de la categoría, subcategorías, "end", totales y fila vacía
    var rowsToDelete = categoryEndRow - categoryStartRow;
    sheet.deleteRows(categoryStartRow, rowsToDelete);
    
    return {
      success: true,
      message: "Categoría eliminada exitosamente"
    };
  } catch (error) {
    return {
      success: false,
      message: "Error al eliminar categoría: " + error.toString()
    };
  }
}

function deleteSubcategory(spreadsheetId, sheetName, subcategoryId) {
  try {
    var sheet = SpreadsheetApp.openById(spreadsheetId).getSheetByName(sheetName);
    var values = sheet.getRange(1, 1, sheet.getLastRow(), 3).getValues();
    
    // Encontrar la fila de la subcategoría
    for (var i = 0; i < values.length; i++) {
      if (values[i][1] === subcategoryId && values[i][0] === "" && values[i][2] !== "") {
        sheet.deleteRows(i + 1, 1);
        return {
          success: true,
          message: "Subcategoría eliminada exitosamente"
        };
      }
    }
    
    return {
      success: false,
      message: "Subcategoría no encontrada"
    };
  } catch (error) {
    return {
      success: false,
      message: "Error al eliminar subcategoría: " + error.toString()
    };
  }
}

function doPost(request) {
  try {
    var jsonPayload = JSON.parse(request.postData.contents);
    var action = jsonPayload.action;
    var spreadsheetId = jsonPayload.spreadsheetId;
    var sheetName = jsonPayload.sheetName;
    
    switch (action) {
      case "createCategory":
        return ContentService.createTextOutput(JSON.stringify(
          createCategory(spreadsheetId, sheetName, jsonPayload.categoryName, jsonPayload.categoryId)
        )).setMimeType(ContentService.MimeType.JSON);
        
      case "createSubcategory":
        return ContentService.createTextOutput(JSON.stringify(
          createSubcategory(spreadsheetId, sheetName, jsonPayload.categoryId, jsonPayload.subcategoryName, jsonPayload.subcategoryId)
        )).setMimeType(ContentService.MimeType.JSON);
        
      case "updateCategory":
        return ContentService.createTextOutput(JSON.stringify(
          updateCategory(spreadsheetId, sheetName, jsonPayload.categoryId, jsonPayload.newName)
        )).setMimeType(ContentService.MimeType.JSON);
        
      case "updateSubcategory":
        return ContentService.createTextOutput(JSON.stringify(
          updateSubcategory(spreadsheetId, sheetName, jsonPayload.subcategoryId, jsonPayload.newName)
        )).setMimeType(ContentService.MimeType.JSON);
        
      case "deleteCategory":
        return ContentService.createTextOutput(JSON.stringify(
          deleteCategory(spreadsheetId, sheetName, jsonPayload.categoryId)
        )).setMimeType(ContentService.MimeType.JSON);
        
      case "deleteSubcategory":
        return ContentService.createTextOutput(JSON.stringify(
          deleteSubcategory(spreadsheetId, sheetName, jsonPayload.subcategoryId)
        )).setMimeType(ContentService.MimeType.JSON);
        
      default:
        return ContentService.createTextOutput(JSON.stringify({
          success: false,
          message: "Acción no válida"
        })).setMimeType(ContentService.MimeType.JSON);
    }
  } catch (error) {
    return ContentService.createTextOutput(JSON.stringify({
      success: false,
      message: "Error en el servidor: " + error.toString()
    })).setMimeType(ContentService.MimeType.JSON);
  }
}

