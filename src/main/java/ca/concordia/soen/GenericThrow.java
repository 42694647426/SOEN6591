package ca.concordia.soen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.*;

import ca.concordia.soen.DestructiveWrapping.Visitor;

class GenericThrow {

  public static void main(String[] args) { 
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
	    System.out.println("Total Count of generic throw: " +totalCount);
  }

  static class Visitor extends ASTVisitor {
	  int count = 0;
	  List<String> names = new ArrayList<>();
    @Override
	public boolean visit(MethodDeclaration methodDeclaration) {
		
		for (Object exceptionType: methodDeclaration.thrownExceptionTypes()) {
			if (exceptionType instanceof SimpleType) {
				count += 1;
				int startLine = ((CompilationUnit) methodDeclaration.getRoot()).getLineNumber(methodDeclaration.getStartPosition());
	      	    int endLine = ((CompilationUnit) methodDeclaration.getRoot()).getLineNumber(methodDeclaration.getStartPosition()+methodDeclaration.getLength());
	            String generic = "Possible generic throws found at line:" + startLine;
	            
			}
		}
    	
    	
    	
//      if (clause.getBody().statements().isEmpty()) {
//    	  count +=1;
//    	  int startLine = ((CompilationUnit) clause.getRoot()).getLineNumber(clause.getStartPosition());
//      	  int endLine = ((CompilationUnit) clause.getRoot()).getLineNumber(clause.getStartPosition()+clause.getLength());
//           String generic = "Possible generic throws found at line:" + startLine;
//           names.add(generic);
//      }
      return true;
    }
  }
}
