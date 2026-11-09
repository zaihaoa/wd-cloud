package com.wd.common.excel.convert;


import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;

import java.util.List;

public class ListStringConverter implements Converter<List<String>> {

    @Override
    public WriteCellData<?> convertToExcelData(List<String> value,
                                               ExcelContentProperty contentProperty,
                                               GlobalConfiguration globalConfiguration) {
        if (value == null || value.isEmpty()) {
            return new WriteCellData<>("");
        }
        return new WriteCellData<>(String.join(",", value));
    }
}
