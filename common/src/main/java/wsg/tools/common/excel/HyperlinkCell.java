package wsg.tools.common.excel;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import wsg.tools.common.converter.ConvertFactory;

import static org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined.BLUE;

/**
 * Cell with hyperlink.
 *
 * @author Kingen
 * @since 2020/7/21
 */
public abstract class HyperlinkCell<T, V> implements CellEditable<T, V> {

    @Override
    public void setCell(Cell cell, T t, Workbook workbook, ConvertFactory<Object> factory) {
        CellEditable.setCellValue(cell, factory.convertValue(getValue(t)));
        String address = getAddress(t);
        if (address == null) {
            return;
        }
        Hyperlink hyperlink = workbook.getCreationHelper().createHyperlink(HyperlinkType.URL);
        hyperlink.setAddress(address);
        cell.setHyperlink(hyperlink);
        cell.setCellStyle(getCellStyle(workbook));
    }

    /* todo build once */
    private CellStyle getCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setUnderline(Font.U_SINGLE);
        font.setColor(BLUE.getIndex());
        cellStyle.setFont(font);
        return cellStyle;
    }

    /**
     * Obtains address of the hyperlink.
     *
     * @param t given object
     * @return address
     */
    public abstract String getAddress(T t);
}
