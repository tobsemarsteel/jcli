package com.tmarsteel.jcli.filter;

import com.tmarsteel.jcli.XMLTest;
import com.tmarsteel.jcli.validation.ValidationException;
import java.math.BigDecimal;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author Tobias Marstaller
 */
public class BigDecimalFilterTest extends XMLTest
{
    public BigDecimalFilterTest()
    {
        this.testXML = "BigDecimalFilterTest.xml";
        this.testNodesName = "filter";
    }
    
    @Test
    public void testParseSucceedsWithoutLimit()
        throws ValidationException
    {
        BigDecimalFilter bdf = new BigDecimalFilter();
        
        String number = "432123456789.51251251252435345746745455234121";
        
        Object ret = bdf.parse(number);
        
        assertTrue("BigDecimalFilter did not return an instance of java.math.BigDecimal", BigDecimal.class.isAssignableFrom(ret.getClass()));
        assertEquals(((BigDecimal) ret).toPlainString(), number);
    }
    
    @Test
    public void testParseSucceedsWithLimit()
        throws ValidationException
    {
        BigDecimalFilter bdf = new BigDecimalFilter(
            new BigDecimal("1.00000000000000000056789"),
            new BigDecimal("2.00000000000000000056789")
        );
        
        String number = "1.50000000000000000056789";
        
        Object ret = bdf.parse(number);
        
        assertTrue("BigDecimalFilter did not return an instance of java.math.BigDecimal", BigDecimal.class.isAssignableFrom(ret.getClass()));
        assertEquals(((BigDecimal) ret).toPlainString(), number);
    }

    @Test(expected=ValidationException.class)
    public void testParseFailsWithInputBelowLimit()
        throws ValidationException
    {
        BigDecimalFilter bdf = new BigDecimalFilter(
            new BigDecimal("1.00000000000000000056789"),
            new BigDecimal("2.00000000000000000056789")
        );
        
        String number = "0.50000000000000000056789";
        
        Object ret = bdf.parse(number);
    }
    
    @Test(expected=ValidationException.class)
    public void testParseFailsWithInputAboveLimit()
        throws ValidationException
    {
        BigDecimalFilter bdf = new BigDecimalFilter(
            new BigDecimal("1.00000000000000000056789"),
            new BigDecimal("2.00000000000000000056789")
        );
        
        String number = "2.50000000000000000056789";
        
        Object ret = bdf.parse(number);
    }
    
    @Test(expected=ValidationException.class)
    public void testParseFailsWithNonNumericInputWithoutLimit()
        throws ValidationException
    {
        BigDecimalFilter bdf = new BigDecimalFilter();
        
        String number = "1.0938209380aaaaa";
        
        Object ret = bdf.parse(number);
    }
    
    @Test(expected=ValidationException.class)
    public void testParseFailsWithNonNumericInputWithLimit()
        throws ValidationException
    {
        BigDecimalFilter bdf = new BigDecimalFilter(
            new BigDecimal("1.00000000000000000056789"),
            new BigDecimal("2.00000000000000000056789")
        );
        
        String number = "1.0938209380aaaaa";
        
        Object ret = bdf.parse(number);
    }
    
    @Test
    public void testNodeConstructor()
    {
        BigDecimalFilter filter = new BigDecimalFilter(testNodes.item(0));
        
        assertEquals(filter.getMinValue().toPlainString(), "10.1");
        assertEquals(filter.getMaxValue().toPlainString(), "2000.978765487");
    }
    
    @Test(expected = NumberFormatException.class)
    public void nodeConstructorShouldFailOnNonNumerical()
    {
        BigDecimalFilter filter = new BigDecimalFilter(testNodes.item(1));
    }
}
