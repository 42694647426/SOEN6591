package ca.concordia.soen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.core.dom.*;

import ca.concordia.soen.DestructiveWrapping.Visitor;
import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import com.opencsv.CSVWriter;

class GenericThrow {

  public static void main(String[] args) throws IOException { 
	  ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
	    File file = new File(args[0]);
	    List<String> files = new ArrayList<String>();
	    Filewalker.listOfFiles(file, files);
	    int totalCount = 0;
	    
	    FileWriter csvFile = new FileWriter("GenericException.csv");
        CSVWriter csvWriter = new CSVWriter(csvFile);
	 // write the header row
        csvWriter.writeNext(new String[] {"Filename", "StartLine", "Endline", "CountinFile"});
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
	    	  for (int i=0; i< visitor.names.size(); i++) {
	  	    	System.out.println(filename + ": " + visitor.count);
	  	        System.out.println(visitor.names.get(i));
	  	        
	  	        
	  	        csvWriter.writeNext(new String[] {filename, visitor.startline.get(i), visitor.endline.get(i), Integer.toString(visitor.count)});
	  	        
	  	        
	  	      }
	  	      }
	  	    }
	  	    csvWriter.close();
	  	    csvFile.close();
	  	      System.out.println("Successfully exported Map to CSV file using OpenCSV!");
	    System.out.println("Total Count of generic throw: " +totalCount);
  }

  static class Visitor extends ASTVisitor {
	  int count = 0;
	  List<String> names = new ArrayList<>();
	  List<String> startline = new ArrayList<>();
	  List<String> endline = new ArrayList<>();
    @Override
	public boolean visit(MethodDeclaration node) {
    	List<Type> methodDeclarationThrownExceptions = node.thrownExceptionTypes();
    	Block body = node.getBody();
    	//System.out.println(body);
    	if(body==null) {
    		return true;
    	}
    	for(Type methodDeclartion: methodDeclarationThrownExceptions){
    		//System.out.println(methodDeclarationThrownExceptions.get(0).toString());
            if (methodDeclartion.toString().equals("Exception") || methodDeclartion.toString().equals("RuntimeException") || methodDeclartion.toString().equals("Throwable")) {
            	//System.out.println("true");
            	int startLine = ((CompilationUnit) node.getRoot()).getLineNumber(node.getStartPosition());
           	  int endLine = ((CompilationUnit) node.getRoot()).getLineNumber(node.getStartPosition())+2;
                String generic = "Possible generic found at line:" + startLine+" Endline: " + endLine;
                count+=1;
                //System.out.println(generic);
                names.add(generic);
                //System.out.println(names);
                startline.add(Integer.toString(startLine));
                endline.add(Integer.toString(endLine));
            }
        }
    	
    	 for(Object statement: body.statements()){
             if (statement instanceof ThrowStatement) {
            	 ThrowStatement throwStatement = (ThrowStatement) statement;
                 //String throwExpression = throwStatement.getExpression().toString();
                 
                 if (throwStatement.getExpression() instanceof ClassInstanceCreation) {
                     ClassInstanceCreation cic = (ClassInstanceCreation) throwStatement.getExpression();
                     if (cic.getType() instanceof SimpleType) {
                         SimpleType st = (SimpleType) cic.getType();
                         if ("Exception".equals(st.getName().getFullyQualifiedName()) || "RuntimeException".equals(st.getName().getFullyQualifiedName()) || "Throwable".equals(st.getName().getFullyQualifiedName())) {
                        	 //System.out.println("here"+throwStatement.getStartPosition());
                        	 //System.out.println("Exception Type: " + st.getName().getFullyQualifiedName());
                        	 int startLine = ((CompilationUnit) node.getRoot()).getLineNumber(throwStatement.getStartPosition());
                         	  int endLine = ((CompilationUnit) node.getRoot()).getLineNumber(throwStatement.getStartPosition()+throwStatement.getLength());
                              String generic = "Possible generic found at line:" + startLine+" Endline: " + endLine;
                              count+=1;
                              names.add(generic);
                              startline.add(Integer.toString(startLine));
                              endline.add(Integer.toString(endLine));
                         }
                     }
                 }
                     
                 
             }
         }
    	
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
