package ca.concordia.soen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.*;

import ca.concordia.soen.CatchClauseCounter.Visitor;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import com.opencsv.CSVWriter;

class DestructiveWrapping {

  public static void main(String[]  args) throws IOException {
	    ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
	    File file = new File(args[0]);
	    List<String> files = new ArrayList<String>();
	    Filewalker.listOfFiles(file, files);
	    int totalCount = 0;
	    FileWriter csvFile = new FileWriter("DestructiveWrap.csv");
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
	    System.out.println("Total Count of destructive wrapping: " +totalCount);
  }


  static class Visitor extends ASTVisitor {
	    int count = 0;
	    List<String> names = new ArrayList<>();
	    List<String> startline = new ArrayList<>();
	    List<String> endline = new ArrayList<>();
	    
	        @Override
	        public boolean visit(CatchClause catchNode) {
	            Statement catchStatement = catchNode.getBody();
	            if (catchStatement instanceof Block && !((Block) catchStatement).statements().isEmpty()) {
	              SimpleName catchException = catchNode.getException().getName();
	              catchStatement.accept(new ASTVisitor() {
	                @Override
	                public boolean visit(ThrowStatement throwNode) {
	                	Expression expression  = throwNode.getExpression();
	                	if(expression instanceof ClassInstanceCreation) {
	                		ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expression;
	                        Type thrownExceptionType = classInstanceCreation.getType();
	                        
	                        if (thrownExceptionType.isSimpleType()) {
	                            SimpleType simpleType = (SimpleType) thrownExceptionType;
	                            SimpleName typeName = (SimpleName) simpleType.getName();
	                            if (!typeName.equals(catchException)) {
	        	                    count += 1;
	        	                    int startLine = ((CompilationUnit) throwNode.getRoot()).getLineNumber(throwNode.getStartPosition());
	        	                    int endLine = ((CompilationUnit) throwNode.getRoot()).getLineNumber(throwNode.getStartPosition() + throwNode.getLength());
	        	                    String destructive = "Possible destructive wrapping found at line:" + startLine;
	        	                    names.add(destructive);
	        	                
	        	                    startline.add(Integer.toString(startLine));
	        	                    endline.add(Integer.toString(endLine));
	                        }
	                
	                  
	                  }
	                  return super.visit(throwNode);
	                }
						return true;
	                }
	              });
	            }
	            return true;
	      }  
	      
	  }


}
