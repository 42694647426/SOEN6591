package ca.concordia.soen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.MethodDeclaration;

class CatchClauseCounter {

  public static void main(String[]  args) {
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

      Visitor visitor = new Visitor();
      root.accept(visitor);

      System.out.println(filename + ": " + visitor.count);

      for (String name : visitor.names) {
        System.out.println(name);
      }
    }
  }


  static class Visitor extends ASTVisitor {
    int count = 0;
    List<String> names = new ArrayList<>();

    @Override
    public boolean visit(CatchClause clause) {
      count += 1;
      names.add(clause.getException().toString());
      return false;
    }
  }

}
