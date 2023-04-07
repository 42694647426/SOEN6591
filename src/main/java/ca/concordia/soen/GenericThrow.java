package ca.concordia.soen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
<<<<<<< HEAD
	public boolean visit(ThrowStatement node) {
        // TODO: the exceptiontype compare to Exception!
    	Type exceptionType = (Type) node.getExpression().resolveTypeBinding().getTypeDeclaration();
		
			if (exceptionType).getFullyQualifiedName().equals(Exception.class.getName())) {
				count += 1;
				int startLine = ((CompilationUnit) node.getRoot()).getLineNumber(node.getStartPosition());
	      	    int endLine = ((CompilationUnit) node.getRoot()).getLineNumber(node.getStartPosition()+node.getLength());
	            String generic = "Possible generic throws found at line:" + startLine;
	           names.add(generic);
			
		}
=======
	public boolean visit(CatchClause node) {
    	
    	SingleVariableDeclaration exceptionDeclaration = node.getException();
        Type exceptionType = exceptionDeclaration.getType();

    	if (isGenericType(exceptionType)) {
    		int startLine = ((CompilationUnit) node.getRoot()).getLineNumber(node.getStartPosition());
      	    int endLine = ((CompilationUnit) node.getRoot()).getLineNumber(node.getStartPosition()+node.getLength());
      	    String generic = "Possible generic throws found at line:" + startLine;
      	    names.add(generic);
    	}
    	
    	
    	
//    	Type exceptionType = (Type) node.getExpression().resolveTypeBinding().getTypeDeclaration();
//		
//			if (exceptionType).getFullyQualifiedName().equals(Exception.class.getName())) {
//				count += 1;
//				int startLine = ((CompilationUnit) node.getRoot()).getLineNumber(node.getStartPosition());
//	      	    int endLine = ((CompilationUnit) node.getRoot()).getLineNumber(node.getStartPosition()+node.getLength());
//	            String generic = "Possible generic throws found at line:" + startLine;
//	           names.add(generic);
//			
//		}
>>>>>>> 8d08f5865143a487abb6b06ae45b02260a0464b4
    	
    	
    	
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
  
  private static boolean isGenericType(Type exceptionType) {
	  List<String> genericExceptions = Arrays.asList(
	            "java.lang.Exception",
	            "java.lang.Throwable",
	            "java.lang.RuntimeException"
	    );
	  if (exceptionType.isSimpleType()) {
	        String typeName = ((SimpleType) exceptionType).getName().getFullyQualifiedName();
	        return genericExceptions.contains(typeName);
	    } else if (exceptionType.isQualifiedType()) {
	        String typeName = ((QualifiedType) exceptionType).getName().getFullyQualifiedName();
	        return genericExceptions.contains(typeName);
	    } else if (exceptionType.isNameQualifiedType()) {
	        String typeName = ((NameQualifiedType) exceptionType).getName().getFullyQualifiedName();
	        return genericExceptions.contains(typeName);
	    }

	    return false;
  }
}
