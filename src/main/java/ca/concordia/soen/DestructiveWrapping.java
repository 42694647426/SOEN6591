package ca.concordia.soen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;

import ca.concordia.soen.CatchClauseCounter.Visitor;

class DestructiveWrapping {

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
	    System.out.println("Total Count of destructive wrapping: " +totalCount);
  }


  static class Visitor extends ASTVisitor {
    int count = 0;
    List<String> names = new ArrayList<>();

    @Override
    public boolean visit(CatchClause node) {
    			
                Statement statement = node.getBody();
                if (statement instanceof Block) {
                    Block block = (Block) statement;
                    for (Object obj : block.statements()) {
                        if (obj instanceof ThrowStatement) {
                        	ThrowStatement throwStatement = (ThrowStatement) obj;
                            if (!throwStatement.getExpression().toString().contains(node.getException().getName().getIdentifier()))  {
                                	count +=1;
                                	//System.out.println("Method:"+node.getName());
                              	  int startLine = ((CompilationUnit) node.getRoot()).getLineNumber(node.getStartPosition());
                              	  int endLine = ((CompilationUnit) node.getRoot()).getLineNumber(node.getStartPosition()+node.getLength());
                                   String destructive = "Possible destructive wrapping found at line:" + startLine;
                                   names.add(destructive);
                                }
                            }
                        }
                    }
                
                return super.visit(node);
            
        
    }
  }

}
