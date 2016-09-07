package com.tmarsteel.jcli.validation.configuration.xml;

import com.tmarsteel.jcli.ParseException;
import com.tmarsteel.jcli.filter.BigDecimalFilter;
import com.tmarsteel.jcli.filter.BigIntegerFilter;
import com.tmarsteel.jcli.filter.DecimalFilter;
import com.tmarsteel.jcli.filter.IntegerFilter;
import com.tmarsteel.jcli.validation.MisconfigurationException;
import com.tmarsteel.jcli.validation.ValidationException;
import org.w3c.dom.Node;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Utility methods used to parse the XML configurations of filters provided by the library
 * @see com.tmarsteel.jcli.filter
 */
abstract class FilterParsingUtil
{
    public static IntegerFilter parseIntegerFilter(XMLValidatorConfigurator context, Node node)
            throws MisconfigurationException, ParseException
    {
        String[] minMaxRadix = XMLValidatorConfigurator.XMLUtils.getMinMaxRadix(node);
        int radix;
        long min;
        long max;

        try
        {
            if (minMaxRadix[2] == null)
            {
                radix = 10;
            }
            else
            {
                radix = Integer.parseInt(minMaxRadix[2]);
            }
        }
        catch (NumberFormatException ex)
        {
            throw new ParseException("Invalid radix: " + minMaxRadix[2]);
        }

        min = minMaxRadix[0] == null? Long.MIN_VALUE : XMLValidatorConfigurator.XMLUtils.asLong(minMaxRadix[0], radix);
        max = minMaxRadix[1] == null? Long.MAX_VALUE : XMLValidatorConfigurator.XMLUtils.asLong(minMaxRadix[1], radix);

        return new IntegerFilter(min, max, radix);
    }

    public static DecimalFilter parseDecimalFilter(Node filterNode)
        throws ValidationException
    {
        String[] minMax = XMLValidatorConfigurator.XMLUtils.getMinMax(filterNode);
        double minValue = XMLValidatorConfigurator.XMLUtils.asDouble(minMax[0]);
        double maxValue = XMLValidatorConfigurator.XMLUtils.asDouble(minMax[1]);
        return new DecimalFilter(minValue, maxValue);
    }

    public BigIntegerFilter parseBigInteger(Node filterNode)
        throws ParseException
    {
        final String[] minMaxRadix = XMLValidatorConfigurator.XMLUtils.getMinMaxRadix(filterNode);
        final int radix;

        try
        {
            if (minMaxRadix[2] == null)
            {
                radix = 10;
            }
            else
            {
                radix = Integer.parseInt(minMaxRadix[2]);
            }
        }
        catch (NumberFormatException ex)
        {
            throw new ParseException("Invalid radix: " + minMaxRadix[2]);
        }

        BigInteger minValue = minMaxRadix[0] == null? null : new BigInteger(minMaxRadix[0], radix);
        BigInteger maxValue = minMaxRadix[1] == null? null : new BigInteger(minMaxRadix[1], radix);
        return new BigIntegerFilter(minValue, maxValue, radix);
    }

    public static BigDecimalFilter parseBigDecimalFilter(Node filterNode)
    {
        String[] minMax = XMLValidatorConfigurator.XMLUtils.getMinMax(filterNode);
        BigDecimal minValue = minMax[0] == null? null : new BigDecimal(minMax[0]);
        BigDecimal maxValue = minMax[1] == null? null : new BigDecimal(minMax[1]);
        return new BigDecimalFilter(minValue, maxValue);
    }


}
