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
	    public boolean visit(CatchClause node) {			
		    Statement statement = node.getBody();
		    if (statement instanceof Block && !((Block) statement).statements().isEmpty()) {
		        Block block = (Block) statement;
		        for (Object obj : block.statements()) {
		            if (obj instanceof ThrowStatement) {
		            	ThrowStatement throwStatement = (ThrowStatement) obj;
		            	String throwExpression = throwStatement.getExpression().toString();
		            	String catchExceptionType = node.getException().getType().toString();
		                if ((!throwExpression.contains(catchExceptionType) && 
		                		(!throwExpression.contains(" Exception")) && 
		                		(!throwExpression.contains("RuntimeException")) && 
		                		(!throwExpression.contains("Throwable"))))  {
		                	count +=1;
	                  	  	int startLine = ((CompilationUnit) node.getRoot()).getLineNumber(node.getStartPosition());
	                  	    int endLine = ((CompilationUnit) node.getRoot()).getLineNumber(node.getStartPosition()+node.getLength());
	                        String destructive = "Possible destructive wrapping found at line:" + startLine;
	                        names.add(destructive);
		                    startline.add(Integer.toString(startLine));
		                    endline.add(Integer.toString(endLine));
	                    }
	                }
	            }
	        }    
		    return super.visit(node); 
	    }
    }
}
