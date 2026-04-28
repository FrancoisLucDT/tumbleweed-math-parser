package darformula;

import static org.junit.Assert.*;

import org.junit.Test;

public class DARFormulaTest {

    @Test
    public void testCheckFormula() {
        System.out.println("testCheckFormula");
        RuntimeFormula.FormulaTokens standard = new RuntimeFormula.FormulaTokens();
        standard.add(new RuntimeFormula.Token("("));
        standard.add(new RuntimeFormula.Token("3"));
        standard.add(new RuntimeFormula.Token("+"));
        standard.add(new RuntimeFormula.Token("x"));
        standard.add(new RuntimeFormula.Token(")"));
        standard.add(new RuntimeFormula.Token("*"));
        standard.add(new RuntimeFormula.Token("5"));
        standard.add(new RuntimeFormula.Token("^"));
        standard.add(new RuntimeFormula.Token("-"));
        standard.add(new RuntimeFormula.Token("log"));
        standard.add(new RuntimeFormula.Token("("));
        standard.add(new RuntimeFormula.Token("y"));
        standard.add(new RuntimeFormula.Token(")"));
        try {
            assert (standard.equals(RuntimeFormula.FormulaTokens.Tokenize("(3 + x)*5^-log(y)")));
        } catch (UnexpectedCharacterException e) {
            e.printStackTrace();
            fail("The code should have recognized every character");
        }
        try {
            standard.checkFormula();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.out.println(standard.toString());
            fail("The code should have accepted standard");
        }
        try {
            RuntimeFormula.FormulaTokens.Tokenize("(3 + x)*5^-log(y)").checkFormula();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.out.println(standard.toString());
            fail("The code should have accepted (3 + x)*5^-log(y)");
        }
        System.out.println(RuntimeFormula.FormulaTokens.Tokenize("(3 + x)*5^-log(y)").toString());
        System.out.println(RuntimeFormula.FormulaTokens.Tokenize("(3 + x)*5^-log(y)").checkFormula().makePostFix().toString());

//		try
//		{	
//			standard2.setFormula("(3 + x)*5^-log(y");
//			fail("(3 + x)*5^-log(y is not refused");
//		} catch (UnexpectedCharacterException e)
//		{
//			e.printStackTrace();
//			fail("setFormula should have recognized every character");
//		} catch (UnexpectedTokenException e)
//		{
//			e.printStackTrace();
//			System.out.println(standard.toString());
//			fail("checkFormula should have thrown an UnevenParenthesesException");
//		} catch (UnevenParenthesesException e)
//		{
//			System.out.println("Good work, team");
//		}
        Class<?> expectedException = new UnevenParenthesesException().getClass();
        checkBadFormula("(3 + x)*5^-log(y", expectedException.getName());

        expectedException = new UnexpectedTokenException().getClass();
        checkBadFormula("(3 + x)*5^-log(y)5", expectedException.getName());

        expectedException = new UnexpectedEOLException().getClass();
        checkBadFormula("", expectedException.getName());

        expectedException = new UnexpectedTokenException().getClass();
        checkBadFormula("x+5sin(cosh(e+ln(x)))", expectedException.getName());
    }

    public void checkBadFormula(String formula, String expectedException) {
        RuntimeFormula standard = new RuntimeFormula();

        try {
            standard.setFormula(formula, new String[]{"x", "y", "z"});
            System.out.println(standard.toString());
            fail("The formula is not refused");
        } catch (UnexpectedCharacterException e) {
            System.out.println(e);
            if (!e.getClass().getName().equals(expectedException))
                fail("Wrong exception\n");
        } catch (UnexpectedTokenException e) {
            System.out.println(e);
            if (!e.getClass().getName().equals(expectedException))
                fail("Wrong exception\n");
        } catch (UnevenParenthesesException e) {
            System.out.println(e);
            if (!e.getClass().getName().equals(expectedException))
                fail("Wrong exception\n");
        } catch (UnexpectedEOLException e) {
            System.out.println(e);
            if (!e.getClass().getName().equals(expectedException))
                fail("Wrong exception\n");
        }

    }

    @Test
    public void testRuntimeFormula() {
        System.out.println("testRuntimeFormula");
        RuntimeFormula standard = new RuntimeFormula();
        if (!(standard instanceof RuntimeFormula))
            fail("The constructor for RuntimeFormula does not make the object");
    }

    @Test
    public void testPutVar() {
        System.out.println("testPutVar");
        RuntimeFormula standard = new RuntimeFormula();
        standard.putVar("a", 5);
        if (standard.getVar("a") != 5)
            fail("putVar or getVar doesn't work");
    }

    @Test
    public void testSetFormula() {
        System.out.println("testSetFormula");
        RuntimeFormula standard = new RuntimeFormula();
        try {
            standard.setFormula("(3 + x)*5^-log(y)", new String[]{"x", "y", "z"});
            System.out.println(standard.toString());
        } catch (UnexpectedCharacterException e) {
            e.printStackTrace();
            fail("setFormula should have recognized every character");
        } catch (UnexpectedTokenException e) {
            e.printStackTrace();
            System.out.println(standard.toString());
            fail("setFormula should have accepted the tokens");
        }
        try {
            standard.setFormula(standard.formulaToString(), new String[]{"x", "y", "z"});
            System.out.println(standard.formulaToString());
        } catch (UnexpectedCharacterException e) {
            e.printStackTrace();
            fail("setFormula should have recognized every character");
        } catch (UnexpectedTokenException e) {
            e.printStackTrace();
            System.out.println(standard.formulaToString());
            fail("setFormula should have accepted the tokens");
        }
    }

    @Test
    public void testCalcValue() {
        System.out.println("testCalcValue");
        RuntimeFormula primogenitor = new RuntimeFormula();
        primogenitor = new RuntimeFormula(primogenitor.new BinaryElement('*', primogenitor.new BinaryElement('+', primogenitor.new SimpleElement(3), primogenitor.new SimpleElement("x")), primogenitor.new BinaryElement('^', primogenitor.new SimpleElement(5), primogenitor.new UnaryElement('-', primogenitor.new UnaryElement('l', primogenitor.new SimpleElement("y"))))), primogenitor.getAllVars());
        primogenitor.putVar("x", 5);
        primogenitor.putVar("y", 1);
        try {
            if (primogenitor.calcValue() != 8) {
                System.out.println(primogenitor.calcValue());
                System.out.println(primogenitor.formulaToString());
                fail("calcValue does not return the right answer");
            }
        } catch (UnexpectedVariableException e) {
            e.printStackTrace();
            fail("Something wrong with either putVar or calcValue of SimpleElement");
        } finally {
            System.out.println(primogenitor.toString());
        }

        try {
            primogenitor.setFormula("(x=5)*6+(y%2=1)*4", new String[]{"x", "y", "z"});
            if (primogenitor.calcValue() != 10) {
                System.out.println(primogenitor.calcValue());
                System.out.println(primogenitor.formulaToString());
                fail("calcValue does not return the right answer");
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            fail("calcValue raises an exception");
        }
    }

    @Test
    public void testTernary()
    {
        System.out.println("testTernary");
        RuntimeFormula primogenitor = new RuntimeFormula();
        try {
            primogenitor.setFormula("sum(i,5,i)", new String[]{"x", "y", "z"});
            if (primogenitor.calcValue() != 15) {
                System.out.println(primogenitor.calcValue());
                System.out.println(primogenitor.formulaToString());
                fail("calcValue does not return the right answer (1)");
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            fail("calcValue raises an exception (1)");
        }
        try {
            primogenitor.setFormula("sum(i,5,sum(j,i,i+j))", new String[]{"x", "y", "z"});
            if (primogenitor.calcValue() != 90) {
                System.out.println(primogenitor.calcValue());
                System.out.println(primogenitor.formulaToString());
                fail("calcValue does not return the right answer (2)");
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            fail("calcValue raises an exception (2)");
        }
        try {
            primogenitor.setFormula("sum(i,5,i)+sum(i,5,i)", new String[]{"x", "y", "z"});
            if (primogenitor.calcValue() != 30) {
                System.out.println(primogenitor.calcValue());
                System.out.println(primogenitor.formulaToString());
                fail("calcValue does not return the right answer (3)");
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            fail("calcValue raises an exception (3)");
        }
    }

}
