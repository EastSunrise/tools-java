package wsg.tools.common.io.excel.writer;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;

import static org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined.BLUE;

/**
 * Write cell with hyperlink.
 *
 * @author Kingen
 * @since 2020/7/21
 */
public abstract class BaseHyperlinkCellFromGetter<T, V> extends BaseCellFromGetter<T, V> {

    @Override
    public void setCellStyleFromGetter(Cell cell, T t, Workbook workbook) {
        String address = getAddress(t);
        if (address == null) {
            return;
        }
        Hyperlink hyperlink = workbook.getCreationHelper().createHyperlink(getType(t));
        hyperlink.setAddress(address);
        cell.setHyperlink(hyperlink);
        cell.setCellStyle(getCellStyle(workbook));
    }

    /**
     * todo build once
     */
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

    /**
     * Obtains type of the link
     *
     * @param t the given object
     * @return {@link HyperlinkType}
     */
    public abstract HyperlinkType getType(T t);
}
