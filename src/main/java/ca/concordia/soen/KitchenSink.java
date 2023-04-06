package ca.concordia.soen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;
import java.util.HashSet;
import java.util.Set;
public class KitchenSink {
	public static void main(String[]  args) {
		  ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
		    File file = new File(args[0]);
		    List<String> files = new ArrayList<String>();
		    Filewalker.listOfFiles(file, files);
		    int totalCount = 0;
		    
		    for (String filename : files) {
		      String source;
		      try {
		        source = StaticAnalysisDemo.read(filename);
		      } catch (IOException e) {
		        System.err.println(e);
		        continue;
		      }

		      parser.setSource(source.toCharArray());

		      ASTNode root = parser.createAST(null);

		      Visitor visitor = new Visitor();
		      root.accept(visitor);

		      //System.out.println(filename + ": " + visitor.count);
		      if (visitor.count >0) {
		    	  totalCount += visitor.count;
		      for (String name : visitor.names) {
		    	System.out.println(filename + ": " + visitor.count);
		        System.out.println(name);
		      }
		      }
		    }
		    System.out.println("Total Count of kitchen sink: " +totalCount);
	  }


	  static class Visitor extends ASTVisitor {
	    int count = 0;
	    List<String> names = new ArrayList<>();

	    @Override
	    public boolean visit(MethodDeclaration methodDeclaration) {
	    	if (methodDeclaration.thrownExceptionTypes().size() > 2) {
                        	count +=1;
                        	//System.out.println("Method:"+node.getName());
                      	  int startLine = ((CompilationUnit) methodDeclaration.getRoot()).getLineNumber(methodDeclaration.getStartPosition());
                      	  int endLine = ((CompilationUnit) methodDeclaration.getRoot()).getLineNumber(methodDeclaration.getStartPosition()+methodDeclaration.getLength());
                           String sink = "Possible destructive wrapping found at line:" + startLine+" Endline: " + endLine;
                           names.add(sink);
         
                }
            
            return super.visit(methodDeclaration);
	        
	    }
	  }
}
