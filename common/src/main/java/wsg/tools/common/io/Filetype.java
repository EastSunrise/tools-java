package wsg.tools.common.io;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.constant.SignEnum;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Type of a file, based on its suffix.
 *
 * @author Kingen
 * @since 2020/10/8
 */
public enum Filetype {
    /**
     * types
     */
    ASS, AVI, CFG, CLASS, CPP, CSS, CSV, DB, EXE, FLAC, GIF,
    H, HTML, ICO, INI, IPCH, JAVA, JPEG, JS, JSON, LOG, MICRO, MKV, MP3, MP4,
    OBJ, PDF, PNG, PROPERTIES, PY, RAR, RMVB, SLN, SRT, SUO, TLOG, TORRENT, TS, TTF, TXT,
    XLTD, XML, YAML, ZIP;

    private static final Map<String, Filetype> FILE_SUFFIXES =
            Arrays.stream(values()).collect(Collectors.toMap(filetype -> filetype.name().toUpperCase(), filetype -> filetype));
    private static final Map<String, Filetype> FILE_HEADERS = new HashMap<>(16);

    static {
        FILE_HEADERS.put("00000014", MP4);
        FILE_HEADERS.put("00000018", MP4);
        FILE_HEADERS.put("0000001C", MP4);
        FILE_HEADERS.put("00000020", MP4);
        FILE_HEADERS.put("00000024", MP4);
        FILE_HEADERS.put("00000028", MP4);
        FILE_HEADERS.put("00000100", ICO);
        FILE_HEADERS.put("00010000", TTF);
        // todo all microsoft types
        FILE_HEADERS.put("00051607", MICRO);
        FILE_HEADERS.put("0A0A7061", JAVA);
        FILE_HEADERS.put("0A706163", JAVA);
        FILE_HEADERS.put("0A736572", PROPERTIES);
        FILE_HEADERS.put("0A737072", PROPERTIES);
        FILE_HEADERS.put("0D0A2320", PROPERTIES);
        FILE_HEADERS.put("1A45DFA3", MKV);
        FILE_HEADERS.put("2166756E", JS);
        FILE_HEADERS.put("2266756E", JS);
        FILE_HEADERS.put("22757365", JS);
        FILE_HEADERS.put("23212F75", PY);
        FILE_HEADERS.put("2369666E", H);
        FILE_HEADERS.put("23696E63", CPP);
        FILE_HEADERS.put("25504446", PDF);
        FILE_HEADERS.put("27757365", JS);
        FILE_HEADERS.put("2866756E", JS);
        FILE_HEADERS.put("2E524D46", RMVB);
        FILE_HEADERS.put("2E737761", CSS);
        FILE_HEADERS.put("2F2A2A0A", JS);
        FILE_HEADERS.put("300D0A30", SRT);
        FILE_HEADERS.put("312E20E6", TXT);
        FILE_HEADERS.put("32303230", LOG);
        FILE_HEADERS.put("3C212D2D", XML);
        FILE_HEADERS.put("3C21444F", HTML);
        FILE_HEADERS.put("3C21646F", HTML);
        FILE_HEADERS.put("3C3F786D", XML);
        FILE_HEADERS.put("3C636F6D", XML);
        FILE_HEADERS.put("3C70726F", XML);
        FILE_HEADERS.put("3C736372", HTML);
        FILE_HEADERS.put("40636861", CSS);
        FILE_HEADERS.put("464C5601", MKV);
        FILE_HEADERS.put("47401110", TS);
        FILE_HEADERS.put("47494638", GIF);
        FILE_HEADERS.put("49443302", MP3);
        FILE_HEADERS.put("49443303", MP3);
        FILE_HEADERS.put("49443304", MP3);
        FILE_HEADERS.put("4A617661", TXT);
        FILE_HEADERS.put("4C012000", OBJ);
        FILE_HEADERS.put("4C016500", OBJ);
        FILE_HEADERS.put("4C012300", OBJ);
        FILE_HEADERS.put("4D5A9000", EXE);
        FILE_HEADERS.put("504B0304", ZIP);
        FILE_HEADERS.put("52494646", AVI);
        FILE_HEADERS.put("53514C69", DB);
        FILE_HEADERS.put("58444C43", CFG);
        FILE_HEADERS.put("5B2E5368", INI);
        FILE_HEADERS.put("612C6162", CSS);
        FILE_HEADERS.put("63003100", CFG);
        FILE_HEADERS.put("63697479", CSV);
        FILE_HEADERS.put("6431333A", TORRENT);
        FILE_HEADERS.put("64373A63", TORRENT);
        FILE_HEADERS.put("64383A61", TORRENT);
        FILE_HEADERS.put("664C6143", FLAC);
        FILE_HEADERS.put("66756E63", JS);
        FILE_HEADERS.put("6A517565", JS);
        FILE_HEADERS.put("7061636B", JAVA);
        FILE_HEADERS.put("73707269", PROPERTIES);
        FILE_HEADERS.put("73776167", YAML);
        FILE_HEADERS.put("7B0A2020", JSON);
        FILE_HEADERS.put("89504E47", PNG);
        FILE_HEADERS.put("CAFEBABE", CLASS);
        FILE_HEADERS.put("D0CF11E0", MICRO);
        FILE_HEADERS.put("EFBBBF20", LOG);
        FILE_HEADERS.put("FFD8FFE0", JPEG);
        FILE_HEADERS.put("FFFAE10C", MP3);
        FILE_HEADERS.put("FFFAE16C", MP3);
        FILE_HEADERS.put("FFFAE340", MP3);
        FILE_HEADERS.put("FFFAE34C", MP3);
        FILE_HEADERS.put("FFFB9044", MP3);
        FILE_HEADERS.put("FFFB9064", MP3);
        FILE_HEADERS.put("FFFBE000", MP3);
        FILE_HEADERS.put("FFFBE040", MP3);
        FILE_HEADERS.put("FFFBE044", MP3);
        FILE_HEADERS.put("FFFBE060", MP3);
        FILE_HEADERS.put("FFFBE064", MP3);
        FILE_HEADERS.put("FFFBE0C4", MP3);
        FILE_HEADERS.put("FFFBE44C", MP3);
        FILE_HEADERS.put("FFFBE800", MP3);
    }

    /**
     * Obtains real type of the given file by reading its header.
     *
     * @param file given file, must be not null
     * @return type
     */
    public static Filetype getRealType(File file) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            byte[] bytes = inputStream.readNBytes(4);
            StringBuilder builder = new StringBuilder();
            for (byte b : bytes) {
                String hs = Integer.toHexString(b & 0xFF).toUpperCase();
                if (hs.length() < 2) {
                    builder.append(0);
                }
                builder.append(hs);
            }
            String header = builder.toString();
            Filetype filetype = FILE_HEADERS.get(header);
            return Objects.requireNonNull(filetype, String.format("Unknown filetype of %s, file: %s.", header, file.getPath()));
        }
    }

    /**
     * Obtains external type of the given name based on its suffix.
     *
     * @param name name of a file or resource
     * @return type
     */
    public static Filetype getExternalType(String name) {
        name = StringUtils.stripStart(name, null);
        name = StringUtils.stripEnd(name, " .");
        if (name.indexOf(SignEnum.DOT.getC()) < 0) {
            return null;
        }
        String ext = name.substring(name.lastIndexOf(SignEnum.DOT.getC()) + 1).toUpperCase();
        Filetype filetype = FILE_SUFFIXES.get(ext);
        return Objects.requireNonNull(filetype, "Unknown filetype of %s" + name);
    }

    public static SuffixFileFilter fileFilter(Filetype... types) {
        return new SuffixFileFilter(Arrays.stream(types).map(Filetype::suffix).collect(Collectors.toList()), IOCase.SYSTEM);
    }

    public static Filetype[] videoTypes() {
        return new Filetype[]{MP4, MKV, AVI, RMVB, TS};
    }

    public boolean isVideo() {
        return ArrayUtils.contains(videoTypes(), this);
    }

    public String suffix() {
        return name().toLowerCase();
    }
}
