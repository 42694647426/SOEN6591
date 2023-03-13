package ca.concordia.soen;

import java.io.IOException;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;

class EmptyCatchClauseFinder {

  public static void main(String[] args) { 
    ASTParser parser = ASTParser.newParser(AST.getJLSLatest());

    for (String filename : args) {
      String source;
      try {
        source = StaticAnalysisDemo.read(filename);
      } catch (IOException e) {
        System.err.println(e);
        continue;
      }

      parser.setSource(source.toCharArray());
    
      ASTNode root = parser.createAST(null);

      root.accept(new Visitor());
    }
  }

  static class Visitor extends ASTVisitor {
    @Override
    public boolean visit(CatchClause clause) {
      if (clause.getBody().statements().isEmpty()) {
        System.out.println(clause.toString());
      }
      return true;
    }
  }
}
