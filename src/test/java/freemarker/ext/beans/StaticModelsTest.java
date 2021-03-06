package freemarker.ext.beans;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template.Version;

@RunWith(JUnit4.class)
public class StaticModelsTest {

    @Test
    public void modelCaching() throws Exception {
        BeansWrapper bw = new BeansWrapper(new Version(2, 3, 21));
        TemplateHashModel statics = bw.getStaticModels();
        TemplateHashModel s = (TemplateHashModel) statics.get(S.class.getName());
        assertNotNull(s);
        assertNotNull(s.get("F"));
        assertNotNull(s.get("m"));
        try {
            s.get("x");
            fail();
        } catch (TemplateModelException e) {
            assertThat(e.getMessage(), containsString("No such key"));
        }
        
        try {
            statics.get("no.such.ClassExists");
            fail();
        } catch (TemplateModelException e) {
            assertTrue(e.getCause() instanceof ClassNotFoundException);
        }
        
        TemplateModel f = s.get("F");
        assertTrue(f instanceof TemplateScalarModel);
        assertEquals(((TemplateScalarModel) f).getAsString(), "F OK");
        
        TemplateModel m = s.get("m");
        assertTrue(m instanceof TemplateMethodModelEx);
        assertEquals(((TemplateScalarModel) ((TemplateMethodModelEx) m).exec(new ArrayList())).getAsString(), "m OK");
        
        assertSame(s, statics.get(S.class.getName()));
        
        bw.clearClassIntrospecitonCache();
        TemplateHashModel sAfterClean = (TemplateHashModel) statics.get(S.class.getName());
        assertNotSame(s, sAfterClean);
        assertSame(sAfterClean, statics.get(S.class.getName()));
        assertNotNull(sAfterClean.get("F"));
        assertNotNull(sAfterClean.get("m"));
    }
    
    public static class S {
        
        public static final String F = "F OK"; 
        
        public static String m() {
            return "m OK";
        }
        
    }
    
}
