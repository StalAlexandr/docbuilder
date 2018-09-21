package org.eapo.corresp.extraction;

import org.eapo.corresp.xdocument.ListOfQueryGetter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alexandr Stal astal@eapo.org; al_stal@mail.ru
 */
public class HTMLFieldsGetter implements ListOfQueryGetter {

    @Override
    public List get(Path pathToTemplate) {

        Set<String> set = new HashSet();
        FileInputStream fis = null;
        try {

            fis = new FileInputStream(pathToTemplate.toFile());
            FileChannel fc = fis.getChannel();

            // Create a read-only CharBuffer on the file
            ByteBuffer bbuf = fc.map(FileChannel.MapMode.READ_ONLY, 0,
                    (int) fc.size());
            CharBuffer cbuf = Charset.forName("UTF-8").newDecoder().decode(bbuf);
            Pattern pattern = Pattern.compile("[$\\{]+[\\w]+.{1}[\\w]+[\\}]{1}");
            Matcher m = pattern.matcher(cbuf);

            while (m.find()) {
                set.add(m.group().substring(2).split("\\.")[0]);
            }

        } catch (Exception ignored) {
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(HTMLFieldsGetter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        List list = new ArrayList();
        list.addAll(set);
        return list;

    }

}
