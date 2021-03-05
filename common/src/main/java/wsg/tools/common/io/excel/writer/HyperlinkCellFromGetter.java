package wsg.tools.common.io.excel.writer;

import java.util.Objects;
import java.util.function.Function;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Workbook;
import wsg.tools.common.util.function.GetterFunction;

/**
 * Write cell with hyperlink.
 *
 * @author Kingen
 * @since 2020/7/21
 */
public class HyperlinkCellFromGetter<T> extends CellFromGetter<T, String> {

    /**
     * Returns address for this hyperlink
     */
    private final Function<T, String> addrFunc;
    /**
     * Returns type for this hyperlink, {@link HyperlinkType#URL} by default
     */
    private final Function<T, HyperlinkType> typeFunc;

    public HyperlinkCellFromGetter(GetterFunction<T, String> getter,
        Function<T, String> addrFunc,
        Function<T, HyperlinkType> typeFunc) {
        super(getter);
        this.addrFunc = Objects.requireNonNull(addrFunc);
        this.typeFunc = typeFunc == null ? t -> HyperlinkType.URL : typeFunc;
    }

    /**
     * todo build once
     */
    private static CellStyle getCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setUnderline(Font.U_SINGLE);
        font.setColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
        cellStyle.setFont(font);
        return cellStyle;
    }

    @Override
    public void setCellStyleFromGetter(Cell cell, T t, Workbook workbook) {
        String address = addrFunc.apply(t);
        if (address == null) {
            return;
        }
        Hyperlink hyperlink = workbook.getCreationHelper().createHyperlink(typeFunc.apply(t));
        hyperlink.setAddress(address);
        cell.setHyperlink(hyperlink);
        cell.setCellStyle(getCellStyle(workbook));
    }
}
