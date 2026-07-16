package dev.fldt.tumbleweed;

import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

import dev.fldt.tumbleweed.Formula.BinaryFunctions;
import dev.fldt.tumbleweed.Formula.UnaryFunctions;


public class FormulaTest {

    @Test
    public void testCheckFormula() {
        System.out.println("testCheckFormula");
        Formula.FormulaTokens standard = new Formula.FormulaTokens();
        standard.add(new Formula.Token("("));
        standard.add(new Formula.Token("3"));
        standard.add(new Formula.Token("+"));
        standard.add(new Formula.Token("x"));
        standard.add(new Formula.Token(")"));
        standard.add(new Formula.Token("*"));
        standard.add(new Formula.Token("5"));
        standard.add(new Formula.Token("^"));
        standard.add(new Formula.Token("-"));
        standard.add(new Formula.Token("log"));
        standard.add(new Formula.Token("("));
        standard.add(new Formula.Token("y"));
        standard.add(new Formula.Token(")"));
        try {
            assert (standard.equals(Formula.FormulaTokens.Tokenize("(3 + x)*5^-log(y)")));
        } catch (UnexpectedCharacterException e) {
            e.printStackTrace();
            fail("The code should have recognized every character");
        }
        try {
            standard.checkFormula();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.out.println(standard.toString());
            fail("The code should have accepted the standard formula");
        }
        try {
            Formula.FormulaTokens.Tokenize("(3 + x)*5^-log(y)").checkFormula();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.out.println(standard.toString());
            fail("The code should have tokenized \"(3 + x)*5^-log(y)\" without error");
        }
        System.out.println(Formula.FormulaTokens.Tokenize("(3 + x)*5^-log(y)").toString());
        System.out.println(Formula.FormulaTokens.Tokenize("(3 + x)*5^-log(y)").checkFormula().makePostFix().toString());

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
        Formula standard = new Formula();

        try {
            standard.setFormula(formula, new String[]{"x", "y", "z"});
            System.out.println(standard.toString());
            fail("The formula is not refused");
        } catch (UnexpectedCharacterException | UnexpectedTokenException | UnevenParenthesesException | UnexpectedEOLException e) {
            System.out.println(e);
            if (!e.getClass().getName().equals(expectedException))
                fail("Wrong exception. Expected " + expectedException +", got " + e.getClass().getName());
        }

    }

    @Test
    public void testFormula() {
        System.out.println("testFormula");
        Formula standard = new Formula();
        if (!(standard instanceof Formula))
            fail("The constructor for Formula does not make the object");
    }

    @Test
    public void testPutVar() {
        System.out.println("testPutVar");
        Formula standard = new Formula();
        standard.putVar("a", 5);
        if (standard.getVar("a") != 5)
            fail("putVar or getVar doesn't work");
    }

    @Test
    public void testSetFormula() {
        System.out.println("testSetFormula");
        Formula standard = new Formula();
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
        Formula generator = new Formula();
        generator = new Formula(generator.new BinaryNode(BinaryFunctions.MULTIPLY, 
            generator.new BinaryNode(BinaryFunctions.PLUS, generator.new ValueNode(3), generator.new VariableNode("x")), generator.new BinaryNode(BinaryFunctions.EXPONENT, generator.new ValueNode(5), generator.new UnaryNode(UnaryFunctions.NEGATE, generator.new UnaryNode(UnaryFunctions.LOG10, generator.new VariableNode("y"))))), generator.getAllVars());
        generator.putVar("x", 5);
        generator.putVar("y", 1);
        try {
            if (generator.calcValue() != 8) {
                System.out.println(generator.calcValue());
                System.out.println(generator.formulaToString());
                fail("calcValue does not return the right answer");
            }
        } catch (UnexpectedVariableException e) {
            e.printStackTrace();
            fail("Something wrong with either putVar or calcValue of SimpleNode");
        } finally {
            System.out.println(generator.toString());
        }

        try {
            generator.setFormula("(x=5)*6+(y%2=1)*4", new String[]{"x", "y", "z"});
            if (generator.calcValue() != 10) {
                System.out.println(generator.calcValue());
                System.out.println(generator.formulaToString());
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
        Formula primogenitor = new Formula();
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
