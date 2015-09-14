package com.tmarsteel.jcli.examples;

import com.tmarsteel.jcli.validation.Validator;
import com.tmarsteel.jcli.Environment;
import com.tmarsteel.jcli.ParseException;
import com.tmarsteel.jcli.validation.ValidationException;
import com.tmarsteel.jcli.validation.configuration.ValidatorConfigurator;
import com.tmarsteel.jcli.validation.configuration.XMLValidatorConfigurator;
import java.io.File;

import java.io.IOException;
import org.xml.sax.SAXException;

class Examples {
    public static void main(String[] args) {
        Validator inputValidator = new Validator(Environment.UNIX);
        Validator.ValidatedInput input;
    
        try
        {
            (XMLValidatorConfigurator.getInstance(
                Examples.class.getResourceAsStream("cli-config.xml")
            )).configure(inputValidator);
            

            input = inputValidator.parse(args);
        }
        catch (SAXException | IOException ex)
        {
            System.err.println("Failed to load internal configuration");
            System.err.println(ex);
            System.exit(1);
            return;
        }
        catch (ParseException | ValidationException ex)
        {
            System.err.println("Please check your input:");
            System.err.println(ex.getMessage());
            System.exit(1);
            return;
        }
        
        File inputFile = (File) input.getOption("input");
        File outputFile = (File) input.getOption("output");
        String inputEncoding = (String) input.getOption("encoding");
        boolean verbose = input.isFlagSet("verbose");
        
        if (outputFile == null)
        {
            // outputFile = new File(/* exchange .csv for .xls in inputFile here */);
        }
        
        // do the conversion!
    }
}